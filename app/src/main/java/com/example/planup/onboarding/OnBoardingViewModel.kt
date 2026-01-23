package com.example.planup.onboarding

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import com.example.planup.network.repository.ProfileRepository
import com.example.planup.network.repository.TermRepository
import com.example.planup.onboarding.model.GenderModel
import com.example.planup.onboarding.model.TermModel
import com.example.planup.signup.data.Agreement
import com.example.planup.util.ImageResizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageResizer: ImageResizer,
    private val termRepository: TermRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            termRepository.getTerms().onSuccess { terms ->

                val termList = terms.sortedBy { it.order }.map { termItem ->
                    var termDetail = ""
                    termRepository.getTermsDetail(termItem.id)
                        .onSuccess { termDetail = it.content }
                        .onFailWithMessage {
                            Log.e("OnBoardingViewModel", "약관 세부사항 불러오기 실패| id:${termItem.id}, $it")
                            termDetail = ""
                        }

                    TermModel(
                        id = termItem.id,
                        title = "[${if (termItem.isRequired) "필수" else "선택"}] ${termItem.summary}",
                        content = termDetail,
                        isRequired = termItem.isRequired
                    )
                }

                _state.update {
                    it.copy(terms = termList)
                }
            }.onFailWithMessage {
                Log.e("OnBoardingViewModel", "약관 불러오기 실패| $it")
            }
        }
        fetchRandomNickname()
    }

    private val _state: MutableStateFlow<OnBoardingState> = MutableStateFlow(OnBoardingState())
    val state: StateFlow<OnBoardingState> = _state.asStateFlow()

    private val _event: Channel<Event> = Channel(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _snackBarEvent: Channel<SnackBarEvent> = Channel(Channel.BUFFERED)
    val snackBarEvent = _snackBarEvent.receiveAsFlow()

    fun updateEmail(email: String) {
        val isValidEmailFormat = emailRegex.matches(email)

        _state.update {
            it.copy(
                email = email,
                isValidEmailFormat = isValidEmailFormat
            )
        }
    }

    fun updatePassword(password: String) {
        val isValidLength = password.length in 8..20
        val isComplex = password.contains(passwordRegex)

        _state.update {
            it.copy(
                password = password,
                isValidPasswordLength = isValidLength,
                isComplexPassword = isComplex
            )
        }
    }

    fun onTermChecked(checkedTerm: TermModel) {
        _state.update {
            it.copy(
                terms = it.terms.map { term ->
                    if (term.id == checkedTerm.id) term.copy(isChecked = !term.isChecked) else term
                }
            )
        }
    }

    fun onAllTermChecked(checked: Boolean) {
        _state.update {
            it.copy(
                terms = it.terms.map { term ->
                    term.copy(isChecked = checked)
                }
            )
        }
    }

    private fun verifyRequiredTerm(): Boolean {
        val terms = state.value.terms

        val unCheckedRequiredTerms = terms.filterIndexed { idx, term ->
            term.isRequired && !term.isChecked
        }

        if (unCheckedRequiredTerms.isNotEmpty()) {
            viewModelScope.launch {
                _snackBarEvent.send(SnackBarEvent.NotCheckedRequiredTerm)
            }
        }

        return unCheckedRequiredTerms.isEmpty()
    }


    suspend fun checkEmailDuplicated(): Boolean {
        var isDuplicated = true

        viewModelScope.async {
            userRepository.checkEmailAvailable(state.value.email)
                .onSuccess { available ->
                    isDuplicated = !available
                    _state.update { it.copy(isDuplicatedEmail = !available) }
                }
                .onFailWithMessage {
                    //TODO :: 오류 메세지
                    Log.e("OnBoardingViewModel", "이메일 중복 확인 실패| $it")

                    isDuplicated = true
                    _state.update { it.copy(isDuplicatedEmail = true) }
                }
        }.await()

        return isDuplicated
    }

    suspend fun checkEmailVerification(): Boolean {
        var isVerified = false
        val verificationToken = savedStateHandle.get<String>(KEY_VERIFICATION_TOKEN)

        if (verificationToken == null) return false

        viewModelScope.async {
            userRepository.checkEmailVerificationStatus(verificationToken)
                .onSuccess { status ->
                    isVerified = status.verified
                    Log.e("OnBoardingViewModel", "이메일 인증 호출 성공 $status")
                }
                .onFailWithMessage {
                    isVerified = false
                    // TODO:: 오류 메세지
                    Log.e("OnBoardingViewModel", "이메일 인증 실패 $it")
                }
        }.await()

        return isVerified
    }

    fun resendEmailVerification() {
        viewModelScope.launch {
            val token = savedStateHandle.get<String>(KEY_VERIFICATION_TOKEN)

            // TODO:: API 변경되면 수정
            if (token == null) {
                // 최초로 보낸 경우
                userRepository.sendMailForSignup(state.value.email)
                    .onSuccess {
                        savedStateHandle[KEY_VERIFICATION_TOKEN] = it.token
                    }
                    .onFailWithMessage {
                        // TODO:: 오류 메세지

                    }
            } else {
                // 다시 보내는 경우
                userRepository.resendMailForSignup(state.value.email)
                    .onSuccess {
                        savedStateHandle[KEY_VERIFICATION_TOKEN] = it.token
                    }
                    .onFailWithMessage {
                        // TODO:: 오류 메세지

                    }
            }
        }
    }

    fun kakaoLogin() {
        // TODO:: 카카오 로그인 로직
    }

    fun fetchRandomNickname() {
        viewModelScope.launch {
            profileRepository.getRandomNickname()
                .onSuccess { randomNickname ->
                    _state.update { it.copy(nickname = randomNickname) }
                }
                .onFailWithMessage {
                    Log.e("OnBoardingViewModel", "랜덤 닉네임 받아오기 오류 :$it")
                }
        }
    }

    fun updateName(name: String) {
        val isHangul = isHangulRegex.matches(name)
        val isAlphabet = isAlphabetRegex.matches(name)

        // 한글의 경우엔 최대 글자 수 15자, 영어의 경우 20자 이내
        val lengthLimit = if (isHangul) 15 else if (isAlphabet) 20 else 100

        val isValidNameLength = name.length <= lengthLimit
        val isNameContainsSpecialChar = !isHangul && !isAlphabet

        _state.update {
            it.copy(
                name = name,
                isNameContainsSpecialChar = isNameContainsSpecialChar,
                isValidNameLength = isValidNameLength
            )
        }
    }

    fun updateNickName(nickname: String) {
        val isHangul = isHangulRegex.matches(nickname)
        val isAlphabet = isAlphabetRegex.matches(nickname)

        // 닉네임 길이는 1 ~ 20 글자 제한
        val isValidNickNameLength = nickname.length <= 20

        // 닉네임은 특수문자 허용x
        val isNickNameContainsSpecialChar = !isHangul && !isAlphabet

        _state.update {
            it.copy(
                nickname = nickname,
                isValidNickNameLength = isValidNickNameLength,
                isNickNameContainsSpecialChar = isNickNameContainsSpecialChar
            )
        }
    }

    fun checkNicknameDuplication() {
        if (state.value.nickname.isBlank()) return

        viewModelScope.launch {
            userRepository.checkNicknameDuplicated(state.value.nickname)
                .onSuccess { available ->
                    _state.update { it.copy(isDuplicateNickName = !available, isAvailableNickName = available) }
                }
                .onFailWithMessage {
                    // TODO:: 에러 메세지
                    Log.e("OnBoardingViewModel", "닉네임 중복 확인 실패| $it")

                    _state.update { it.copy(isDuplicateNickName = true) }
                }
        }
    }

    fun updateGender(gender: GenderModel) {
        _state.update { it.copy(gender = gender) }
    }

    fun updateYear(year: Int) {
        _state.update {
            it.copy(
                months = (1..12).toList(),
                days = emptyList(),
                year = year,
                month = 0,
                day = 0
            )
        }
    }

    fun updateProfileImage(some: Any) {
        viewModelScope.launch {
            val tempFile = imageResizer.saveToTempFile(some)

            _state.update {
                it.copy(profileImage = tempFile?.toURI().toString())
            }
        }
    }

    fun updateMonth(month: Int) {
        _state.update {
            it.copy(
                days = (1..YearMonth.of(it.year, month).atEndOfMonth().dayOfMonth).toList(),
                month = month,
                day = 0,
            )
        }
    }

    fun updateDay(day: Int) {
        _state.update {
            it.copy(
                day = day
            )
        }
    }

    fun signup() {
        viewModelScope.launch {
            if(state.value.loading) return@launch

            _state.update { it.copy(loading = true) }

            userRepository.signup(
                email = state.value.email,
                password = state.value.password,
                passwordCheck = state.value.password,
                nickname = state.value.nickname,
                gender = if(state.value.gender == GenderModel.Male) "MALE" else "FEMALE",
                profileImg = state.value.profileImage,
                agreements = state.value.terms.map { Agreement(it.id, it.isChecked) }
            ).onSuccess {
                _event.send(Event.FinishSignup)
                _state.update { it.copy(loading = false) }
            }.onFailWithMessage {
                // TODO:: 에러 메세지

                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun proceedNextStep(current: OnboardingStep) {
        viewModelScope.launch {
            val next = current.getNextStep()

            when (current) {
                OnboardingStep.Term -> {
                    if (verifyRequiredTerm())
                        sendNavigateEvent(next)
                }

                OnboardingStep.Id -> {
                    if (!checkEmailDuplicated()) {
                        sendNavigateEvent(next)
                    }
                }

                OnboardingStep.Password -> {
                    if (state.value.isValidPasswordLength && state.value.isComplexPassword){
                        resendEmailVerification()
                        sendNavigateEvent(next)
                    }
                }

                OnboardingStep.Verification -> {
                    if (checkEmailVerification()) {
                        sendNavigateEvent(next)
                    } else {
                        // TODO:: 오류 메세지
                    }
                }

                OnboardingStep.Profile -> {
                    // 이름, 닉네임, 성별, 생년월일이 모두 올바르게 입력된 경우 전환
                    if (
                        state.value.name.isNotEmpty() &&
                        state.value.nickname.isNotEmpty() &&
                        state.value.gender != GenderModel.Unknown &&
                        state.value.year != 0 &&
                        state.value.month != 0 &&
                        state.value.day != 0 &&
                        !state.value.isNameContainsSpecialChar &&
                        state.value.isValidNameLength &&
                        state.value.isValidNickNameLength &&
                        !state.value.isDuplicateNickName &&
                        state.value.isAvailableNickName
                    )
                        signup()
                    else {
                        _snackBarEvent.send(SnackBarEvent.ProfileNotFilled)

                    }
                }
            }
        }
    }

    private suspend fun sendNavigateEvent(step: OnboardingStep?) {
        if(step == null) return
        _event.send(Event.Navigate(step))
    }

    sealed class Event {
        data class Navigate(val step: OnboardingStep) : Event()
        data object FinishSignup : Event()
    }

    sealed class SnackBarEvent {
        data object NotCheckedRequiredTerm : SnackBarEvent()
        data object InvalidInviteCode : SnackBarEvent()
        data object ProfileNotFilled : SnackBarEvent()
    }

    private companion object {
        private val emailRegex = Patterns.EMAIL_ADDRESS.toRegex()
        private val passwordRegex = Regex("""[!"#$%&'()*+,\-./:;<=>?@\[₩\]^_`{|}~]""")
        private val isAlphabetRegex = Regex("""^[a-zA-Z ]+$""")
        private val isHangulRegex = Regex("""^[가-힣 ]+$""")

        private const val KEY_VERIFICATION_TOKEN = "key_verification_token"
    }
}

data class OnBoardingState(
    val terms: List<TermModel> = listOf(),
    val email: String = "",
    val isValidEmailFormat: Boolean = true,
    val isDuplicatedEmail: Boolean = false,
    val password: String = "",
    val isValidPasswordLength: Boolean = false,
    val isComplexPassword: Boolean = false,
    val name: String = "",
    val isValidNameLength: Boolean = true,
    val isNameContainsSpecialChar: Boolean = false,
    val nickname: String = "",
    val isValidNickNameLength: Boolean = true,
    val isNickNameContainsSpecialChar: Boolean = false,
    val isDuplicateNickName: Boolean = false,
    val isAvailableNickName: Boolean = false,
    val profileImage: String = "",
    val gender: GenderModel = GenderModel.Unknown,
    val years: List<Int> = YearMonth.now().year.let {
        (it downTo it - 100).toList()
    },
    val months: List<Int> = listOf(),
    val days: List<Int> = listOf(),
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val inviteCode: String = "",
    val loading: Boolean = false,
)

sealed class OnboardingStep(val step: Int, val title: String?) {
    private val totalStep: Int = 5

    private val values: List<OnboardingStep> = listOf(
        Term,
        Id,
        Password,
        Verification,
        Profile
    )

    data object Term : OnboardingStep(step = 1, title = null)
    data object Id : OnboardingStep(step = 2, title = null)
    data object Password : OnboardingStep(step = 3, title = null)
    data object Verification : OnboardingStep(step = 4, title = null)
    data object Profile : OnboardingStep(step = 5, title = "프로필 설정")

    fun getFloatProgress(): Float {
        val currentProgress = this.step
        return currentProgress / totalStep.toFloat()
    }

    fun getRoute(): Any {
        return when (this) {
            Term -> OnBoardTermRoute
            Id -> OnBoardIdRoute
            Password -> OnBoardPasswordRoute
            Verification -> OnBoardVerificationRoute
            Profile -> OnBoardProfileRoute
        }
    }

    fun getNextStep(): OnboardingStep? {
        return values.getOrNull(this.step)
    }

    companion object {
        fun parseRoute(route: String): OnboardingStep {
            return when {
                route.contains(OnBoardTermRoute::class.qualifiedName.toString()) -> Term
                route.contains(OnBoardIdRoute::class.qualifiedName.toString()) -> Id
                route.contains(OnBoardPasswordRoute::class.qualifiedName.toString()) -> Password
                route.contains(OnBoardVerificationRoute::class.qualifiedName.toString()) -> Verification
                route.contains(OnBoardProfileRoute::class.qualifiedName.toString()) -> Profile
                else -> Term
            }
        }
    }
}