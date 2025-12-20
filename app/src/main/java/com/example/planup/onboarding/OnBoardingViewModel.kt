package com.example.planup.onboarding

import androidx.lifecycle.ViewModel
import com.example.planup.onboarding.model.TermModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(

): ViewModel() {

    private val _state: MutableStateFlow<OnBoardingState> = MutableStateFlow(OnBoardingState())
    val state: StateFlow<OnBoardingState> = _state.asStateFlow()

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

    data object Term: OnboardingStep(step = 1, title = "")
    data object Id: OnboardingStep(step = 2, title = "")
    data object Password: OnboardingStep(step = 3, title = "")
    data object Verification: OnboardingStep(step = 4, title = "")
    data object Profile: OnboardingStep(step = 5, title = "")
    data object ShareFriendCode: OnboardingStep(step = 6, title = "")
    data object ShareInvite: OnboardingStep(step = 7, title = "")

    fun getStep(step: Int): OnboardingStep {
        return when(step) {
            Term.step -> Term
            Id.step -> Id
            Password.step -> Password
            Verification.step -> Verification
            Profile.step -> Profile
            ShareFriendCode.step -> ShareFriendCode
            ShareInvite.step -> ShareInvite
            else -> { throw Exception("알 수 없는 단계") }
        }
    }

    fun getFloatProgress(): Float {
        val currentProgress = this.step
        return  totalStep.toFloat() / currentProgress
    }
}