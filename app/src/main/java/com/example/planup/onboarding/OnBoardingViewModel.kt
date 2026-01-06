package com.example.planup.onboarding

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.onboarding.model.GenderModel
import com.example.planup.onboarding.model.TermModel
import com.example.planup.util.ImageResizer
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val imageResizer: ImageResizer

) : ViewModel() {
    private val _state: MutableStateFlow<OnBoardingState> = MutableStateFlow(OnBoardingState())
    val state: StateFlow<OnBoardingState> = _state.asStateFlow()

    private val _event: Channel<Event> = Channel(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _snackBarEvent: Channel<SnackBarEvent> = Channel(Channel.BUFFERED)
    val snackBarEvent = _snackBarEvent.receiveAsFlow()

    fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email
            )
        }
    }

    fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password
            )
        }
        viewModelScope.launch {
            sendNavigateEvent(OnboardingStep.Verification)
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

    fun verifyRequiredTerm(): Boolean {
        val terms = state.value.terms

        val unCheckedRequiredTerms = terms.filterIndexed { idx, term ->
            term.isRequired && !term.isChecked
        }

        if (unCheckedRequiredTerms.isEmpty()) {
            viewModelScope.launch {
                sendNavigateEvent(OnboardingStep.Id)
            }
        } else {
            viewModelScope.launch {
                _snackBarEvent.send(SnackBarEvent.NotCheckedRequiredTerm)
            }
        }

        return unCheckedRequiredTerms.isEmpty().also { isEmpty ->
            if (!isEmpty) {
                viewModelScope.launch {
                    _snackBarEvent.send(SnackBarEvent.NotCheckedRequiredTerm)
                }
            }
        }
    }

    fun validateEmailFormat(email: String) {
        _state.update {
            it.copy(
                isValidEmailFormat = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )
        }
    }

    fun checkEmailDuplicated(email: String) {
        viewModelScope.launch {
            // TODO:: 이메일 중복 확인 API
            var isDuplicated = false

            _state.update { it.copy(isDuplicatedEmail = isDuplicated) }

            if (!isDuplicated) {
                updateEmail(email)
                sendNavigateEvent(OnboardingStep.Password)
            }
        }
    }

    fun validatePasswordFormat(password: String) {
        val isValidLength = password.length in 8..20
        val isComplex = password.contains(passwordRegex)

        _state.update {
            it.copy(
                isValidPasswordLength = isValidLength,
                isComplexPassword = isComplex
            )
        }
    }

    fun checkVerificationState() {
        viewModelScope.launch {
            // TODO:: 이메일 인증 여부 확인

            sendNavigateEvent(OnboardingStep.Profile)
        }
    }

    fun resendEmailVerification() {
        // TODO:: 이메일 재전송 로직
    }

    fun kakaoLogin() {
        // TODO:: 카카오 로그인 로직
    }

    fun updateName(name: String) {
        val isValidNameLength = name.length <= 20
        val isNameContainsSpecialChar = name.contains(nameRegex)

        _state.update {
            it.copy(
                name = name,
                isNameContainsSpecialChar = isNameContainsSpecialChar,
                isValidNameLength = isValidNameLength
            )
        }
    }

    fun updateNickName(nickname: String) {
        // TODO:: 닉네임 중복 확인 API

        val isDuplicateNickName = false
        // 닉네임 길이는 1 ~ 20 글자 제한
        val isValidNickNameLength = nickname.length <= 20

        _state.update {
            it.copy(
                nickname = nickname,
                isDuplicateNickName = isDuplicateNickName,
                isValidNickNameLength = isValidNickNameLength,
            )
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

    fun foo(some: Any) {
        viewModelScope.launch {
            val file = imageResizer.saveToTempFile(some)
            println(file?.toURI())
            _state.update {
                it.copy(profileImage = file?.toURI().toString())
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

    fun proceedNextStep(current: OnboardingStep) {
        viewModelScope.launch {
            val next = current.getNextStep()

            if (next == null) {
                Log.d("OnBoardingViewModel", "화면 전환 오류: 다음 화면이 존재하지 않음")
            } else {
                sendNavigateEvent(next)
            }
        }
    }

    private suspend fun sendNavigateEvent(step: OnboardingStep) {
        _event.send(Event.Navigate(step))
    }

    sealed class Event {
        data class Navigate(val step: OnboardingStep) : Event()
    }

    sealed class SnackBarEvent {
        data object NotCheckedRequiredTerm : SnackBarEvent()
        data object InvalidInviteCode : SnackBarEvent()
    }

    private companion object {
        private val passwordRegex = Regex("""[!"#$%&'()*+,\-./:;<=>?@\[₩\]^_`{|}~]""")
        private val nameRegex = Regex("[^a-zA-Z0-9가-힣 ]")
    }
}

data class OnBoardingState(
    // TODO:: 약관 연결 후 임시 데이터 제거
    val terms: List<TermModel> = listOf(
        TermModel(
            id = 1,
            title = "필수이용약관1",
            content = "세부사항1",
            isRequired = true
        ),
        TermModel(
            id = 2,
            title = "선택이용약관2",
            content = "세부사항2세\n\n\n\n\n부사항2세부사항2세부사항2세부사항2세부사항2세부사항2\n\n세부사항2\n\n세부사\n\n\n\n항2세부사\n\n\n\n\n\n\n\n항aaasdasdasdasdasdasd2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2",
            isRequired = false
        ),
        TermModel(
            id = 3,
            title = "필수이용약관1",
            content = "세부사항1",
            isRequired = true
        ),
        TermModel(
            id = 4,
            title = "선택이용약관2",
            content = "세부사항2",
            isRequired = false
        ),
        TermModel(
            id = 5,
            title = "필수이용약관1",
            content = "세부사항1",
            isRequired = true
        ),
        TermModel(
            id = 6,
            title = "선택이용약관2",
            content = "세부사항2",
            isRequired = false
        ),
    ),
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
    val isDuplicateNickName: Boolean = false,
    val profileImage: String = "",
    val gender: GenderModel = GenderModel.Unknown,
    val years: List<Int> = YearMonth.now().year.let {
        (it downTo it - 100).toList()
    },
    val months: List<Int> = listOf(),
    val days: List<Int> = listOf(),
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0
)

sealed class OnboardingStep(val step: Int, val title: String?) {
    private val totalStep: Int = 7

    private val values: List<OnboardingStep> = listOf(
        Term,
        Id,
        Password,
        Verification,
        Profile,
        ShareFriendCode,
        ShareInvite
    )

    data object Term : OnboardingStep(step = 1, title = null)
    data object Id : OnboardingStep(step = 2, title = null)
    data object Password : OnboardingStep(step = 3, title = null)
    data object Verification : OnboardingStep(step = 4, title = null)
    data object Profile : OnboardingStep(step = 5, title = "프로필 설정")
    data object ShareFriendCode : OnboardingStep(step = 6, title = null)
    data object ShareInvite : OnboardingStep(step = 7, title = null)

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
            ShareFriendCode -> OnBoardShareFriendCodeRoute
            ShareInvite -> OnBoardShareInviteRoute
        }
    }

    fun getNextStep(): OnboardingStep? {
        return if (this != values.last()) {
            values[this.step]
        } else {
            null
        }
    }

    companion object {
        fun parseRoute(route: String): OnboardingStep {
            return when {
                route.contains(OnBoardTermRoute::class.qualifiedName.toString()) -> Term
                route.contains(OnBoardIdRoute::class.qualifiedName.toString()) -> Id
                route.contains(OnBoardPasswordRoute::class.qualifiedName.toString()) -> Password
                route.contains(OnBoardVerificationRoute::class.qualifiedName.toString()) -> Verification
                route.contains(OnBoardProfileRoute::class.qualifiedName.toString()) -> Profile
                route.contains(OnBoardShareFriendCodeRoute::class.qualifiedName.toString()) -> ShareFriendCode
                route.contains(OnBoardShareInviteRoute::class.qualifiedName.toString()) -> ShareInvite
                else -> Term
            }
        }
    }
}