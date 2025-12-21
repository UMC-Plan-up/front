package com.example.planup.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.onboarding.model.TermModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(

): ViewModel() {
    private val _state: MutableStateFlow<OnBoardingState> = MutableStateFlow(OnBoardingState())
    val state: StateFlow<OnBoardingState> = _state.asStateFlow()


    private val _event: Channel<Event> = Channel(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _snackBarEvent: Channel<SnackBarEvent> = Channel(Channel.BUFFERED)
    val snackBarEvent = _snackBarEvent.receiveAsFlow()

    fun verifyRequiredTerm(checked: List<Boolean>): Boolean {
        val terms = state.value.terms

        val unCheckedRequiredTerms = terms.filterIndexed { idx, term ->
            term.isRequired && !checked[idx]
        }

        return unCheckedRequiredTerms.isEmpty().also { isEmpty ->
            if(!isEmpty) {
                viewModelScope.launch {
                    _snackBarEvent.send(SnackBarEvent.NotCheckedRequiredTerm)
                }
            }
        }
    }

    sealed class Event {

    }

    sealed class SnackBarEvent {
        data object NotCheckedRequiredTerm: SnackBarEvent()
        data object InvalidInviteCode: SnackBarEvent()
    }
}

data class OnBoardingState(
    val step: OnboardingStep = OnboardingStep.Term,
    val terms: List<TermModel> = listOf(
        TermModel(
            id = 1,
            title = "필수이용약관1",
            content = "세부사항1",
            isRequired = true
        ),
        TermModel(
            id = 1,
            title = "선택이용약관2",
            content = "세부사항2세\n\n\n\n\n부사항2세부사항2세부사항2세부사항2세부사항2세부사항2\n\n세부사항2\n\n세부사\n\n\n\n항2세부사\n\n\n\n\n\n\n\n항aaasdasdasdasdasdasd2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2세부사항2",
            isRequired = false
        ),                TermModel(
            id = 1,
            title = "필수이용약관1",
            content = "세부사항1",
            isRequired = true
        ),
        TermModel(
            id = 1,
            title = "선택이용약관2",
            content = "세부사항2",
            isRequired = false
        ),                TermModel(
            id = 1,
            title = "필수이용약관1",
            content = "세부사항1",
            isRequired = true
        ),
        TermModel(
            id = 1,
            title = "선택이용약관2",
            content = "세부사항2",
            isRequired = false
        ),
    )
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
}