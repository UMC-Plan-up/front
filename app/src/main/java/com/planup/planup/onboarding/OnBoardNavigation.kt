package com.planup.planup.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.planup.planup.onboarding.model.OnboardingStep
import kotlinx.serialization.Serializable

@Composable
fun OnboardNavHost(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: OnBoardingViewModel = hiltViewModel(LocalActivity.current as OnBoardingActivity)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = OnBoardTermRoute::class,
    ) {
        composable<OnBoardTermRoute> {
            OnBoardingTermScreen(
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .consumeWindowInsets(WindowInsets.ime),
                state = state,
                onNext = { viewModel.proceedNextStep(currentStep) },
                onTermChecked = viewModel::onTermChecked,
                onAllTermChecked = viewModel::onAllTermChecked
            )
        }

        composable<OnBoardIdRoute> {
            OnBoardingIdScreen(
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .consumeWindowInsets(WindowInsets.ime),
                state = state,
                onNext = { viewModel.proceedNextStep(currentStep) },
                onEmailChanged = viewModel::updateEmail
            )
        }

        composable<OnBoardPasswordRoute> {
            OnBoardingPasswordScreen(
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .consumeWindowInsets(WindowInsets.ime),
                state = state,
                onNext = { viewModel.proceedNextStep(currentStep) },
                onPasswordChanged = viewModel::updatePassword
            )
        }
        composable<OnBoardVerificationRoute> {
            OnBoardingVerificationScreen(
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .consumeWindowInsets(WindowInsets.ime),
                state = state,
                onNext = { viewModel.proceedNextStep(currentStep) },
                onClickResend = viewModel::resendEmailVerification,
                onClickKaKao = viewModel::requestKakaoLogin
            )
        }

        composable<OnBoardProfileRoute> {
            OnBoardingProfileScreen(
                modifier = modifier,
                state = state,
                onNext = { viewModel.proceedNextStep(currentStep) },
                onNameChanged = viewModel::updateName,
                onNicknameChanged = viewModel::updateNickName,
                onGenderChanged = viewModel::updateGender,
                onNewImageByCamera = viewModel::updateProfileImage,
                onNewImageByPhotoPicker = viewModel::updateProfileImage,
                onYearChanged = viewModel::updateYear,
                onMonthChanged = viewModel::updateMonth,
                onDayChanged = viewModel::updateDay,
                onClickNicknameDuplication = viewModel::checkNicknameDuplication
            )
        }
    }
}

@Serializable
object OnBoardTermRoute

@Serializable
object OnBoardIdRoute

@Serializable
object OnBoardPasswordRoute

@Serializable
object OnBoardVerificationRoute

@Serializable
object OnBoardProfileRoute