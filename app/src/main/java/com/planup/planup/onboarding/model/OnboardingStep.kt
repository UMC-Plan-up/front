package com.planup.planup.onboarding.model

import com.planup.planup.onboarding.OnBoardIdRoute
import com.planup.planup.onboarding.OnBoardPasswordRoute
import com.planup.planup.onboarding.OnBoardProfileRoute
import com.planup.planup.onboarding.OnBoardTermRoute
import com.planup.planup.onboarding.OnBoardVerificationRoute

sealed class OnboardingStep(val step: Int, val title: String?) {
    private val totalStep: Int = 5

    private val normalStep: List<OnboardingStep> = listOf(
        Term,
        Id,
        Password,
        Verification,
        Profile
    )

    private val kakaoSteps: List<OnboardingStep> = listOf(
        Term,
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

    fun getNextStep(signupType: SignupTypeModel): OnboardingStep? {
        return if(signupType is SignupTypeModel.Normal) {
            normalStep.getOrNull(this.step)
        } else {
            kakaoSteps.getOrNull(this.step)
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
                else -> Term
            }
        }
    }
}