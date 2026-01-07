package com.example.planup.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Composable
fun OnboardNavHost(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: OnBoardingViewModel = hiltViewModel(LocalActivity.current as OnBoardingActivity)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    //TODO :: proceedNextStep 사용하도록 변경
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = OnBoardTermRoute::class,
    ) {
        composable<OnBoardTermRoute> {
            OnBoardingTermScreen(
                modifier = modifier,
                state = state,
                onNext = viewModel::verifyRequiredTerm,
                onTermChecked = viewModel::onTermChecked,
                onAllTermChecked = viewModel::onAllTermChecked
            )
        }

        composable<OnBoardIdRoute> {
            OnBoardingIdScreen(
                modifier = modifier,
                state = state,
                onNext = viewModel::checkEmailDuplicated,
                validateEmailFormat = viewModel::validateEmailFormat
            )
        }

        composable<OnBoardPasswordRoute> {
            OnBoardingPasswordScreen(
                modifier = modifier,
                state = state,
                onNext = viewModel::updatePassword,
                validatePasswordFormat = viewModel::validatePasswordFormat
            )
        }
        composable<OnBoardVerificationRoute> {
            OnBoardingVerificationScreen(
                modifier = modifier,
                state = state,
                onNext = viewModel::checkVerificationState,
                onClickResend = viewModel::resendEmailVerification,
                onClickKaKao = viewModel::kakaoLogin
            )
        }

        composable<OnBoardProfileRoute> {
            OnBoardingProfileScreen(
                modifier = modifier,
                state = state,
                onNext = {
                    viewModel.proceedNextStep(currentStep)
                },
                onNameChanged = viewModel::updateName,
                onNicknameChanged = viewModel::updateNickName,
                onGenderChanged = viewModel::updateGender,
                onNewImageByCamera = viewModel::foo,
                onNewImageByPhotoPicker = viewModel::foo,
                onYearChanged = viewModel::updateYear,
                onMonthChanged = viewModel::updateMonth,
                onDayChanged = viewModel::updateDay
            )
        }

        composable<OnBoardShareFriendCodeRoute> {
            OnBoardingShareCodeScreen(
                modifier = modifier,
                state = state,
                onNext = {},
                onShareKakao = {},
                onShareSMS = {}
            )
        }

        composable<OnBoardShareInviteRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    println("last")
                }
            ) {
                Text(text = "OnBoardShareInviteRoute")
            }

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

@Serializable
object OnBoardShareFriendCodeRoute

@Serializable
object OnBoardShareInviteRoute