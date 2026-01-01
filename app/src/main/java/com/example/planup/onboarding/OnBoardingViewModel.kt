package com.example.planup.onboarding

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.onboarding.model.TermModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
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
                    if(term.id == checkedTerm.id) term.copy(isChecked = !term.isChecked) else term
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

        if(unCheckedRequiredTerms.isEmpty()) {
            viewModelScope.launch {
                sendNavigateEvent(OnboardingStep.Id)
            }
        } else {
            viewModelScope.launch {
                _snackBarEvent.send(SnackBarEvent.NotCheckedRequiredTerm)
            }
        }

        return unCheckedRequiredTerms.isEmpty().also { isEmpty ->
            if(!isEmpty) {
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
            // TODO:: 이메일 중복 확인
            var isDuplicated = false

            _state.update { it.copy(isDuplicatedEmail = isDuplicated) }

            if(!isDuplicated) {
                updateEmail(email)
                sendNavigateEvent(OnboardingStep.Password)
            }
        }
    }

    fun validatePasswordFormat(password: String) {
        val isValidLength = password.length in 8..20

        // TODO:: 비밀번호 특수문자 정책 문의
        val isComplex = password.any { it.isDigit()} && password.any { it.isLetter() }

        _state.update { it.copy(
            isValidPasswordLength = isValidLength,
            isComplexPassword = isComplex
        ) }
    }

    private suspend fun sendNavigateEvent(step: OnboardingStep) {
        _state.update { it.copy(step = step) }
        _event.send(Event.Navigate(step))
    }

    sealed class Event {
        data class Navigate(val step: OnboardingStep): Event()
    }

    sealed class SnackBarEvent {
        data object NotCheckedRequiredTerm: SnackBarEvent()
        data object InvalidInviteCode: SnackBarEvent()
    }
}

data class OnBoardingState(
    val step: OnboardingStep = OnboardingStep.Term,
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
        ),                TermModel(
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
        ),                TermModel(
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
)

sealed class OnboardingStep(val step: Int, val title: String?) {
    private val totalStep: Int = 7

    data object Term: OnboardingStep(step = 1, title = null)
    data object Id: OnboardingStep(step = 2, title = null)
    data object Password: OnboardingStep(step = 3, title = null)
    data object Verification: OnboardingStep(step = 4, title = null)
    data object Profile: OnboardingStep(step = 5, title = "프로필 설정")
    data object ShareFriendCode: OnboardingStep(step = 6, title = null)
    data object ShareInvite: OnboardingStep(step = 7, title = null)

    fun getFloatProgress(): Float {
        val currentProgress = this.step
        return currentProgress / totalStep.toFloat()
    }

    fun getRoute(): Any {
        return when(this) {
            Term -> OnBoardTermRoute
            Id -> OnBoardIdRoute
            Password -> OnBoardPasswordRoute
            Verification -> OnBoardVerificationRoute
            Profile -> OnBoardProfileRoute
            ShareFriendCode -> OnBoardShareFriendCodeRoute
            ShareInvite -> OnBoardShareInviteRoute
        }
    }
}