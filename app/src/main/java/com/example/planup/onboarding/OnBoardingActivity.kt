package com.example.planup.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planup.R
import com.example.planup.component.snackbar.GraySnackbarHost
import com.example.planup.onboarding.component.OnBoardingFinishDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity: AppCompatActivity() {

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            var isFinishDialogVisible by remember { mutableStateOf(false) }
            val errorSnackBarHost = remember { SnackbarHostState() }
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentStep = remember(navBackStackEntry) {
                navBackStackEntry?.destination?.route?.let { route ->
                    OnboardingStep.parseRoute(route)
                } ?: OnboardingStep.Term
            }

            Box {
                OnBoardScreen(
                    navController = navController,
                    step = currentStep
                )

                GraySnackbarHost(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(bottom = 60.dp),
                    hostState = errorSnackBarHost
                )
                if(isFinishDialogVisible) {
                    OnBoardingFinishDialog(
                        modifier = Modifier
                            .align(Alignment.Center),
                        onClick = {
                            // 메인 화면을 백스택에 추가하고 이동할지 고려해볼 것
                            isFinishDialogVisible = false
                            startActivity(Intent(this@OnBoardingActivity, CommunityIntroActivity::class.java)
                                .apply {
                                    putExtra("nickname", state.nickname)
                                })
                            finish()
                        }
                    )
                }

            }

            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when(event) {
                        is OnBoardingViewModel.Event.Navigate -> {
                            navController.navigate(event.step.getRoute())
                        }
                        is OnBoardingViewModel.Event.SendCodeWithSMS -> {
                            val shareMessage = """
                                    ${state.nickname}님이 친구 신청을 보냈어요.
                                    Plan-Up에서 함께 목표 달성에 참여해 보세요!
                                    친구 코드: ${state.inviteCode}
                                """.trimIndent()

                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)

                            startActivity(shareIntent)
                        }
                        is OnBoardingViewModel.Event.FinishSignup -> {
                            isFinishDialogVisible = true
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.snackBarEvent.collect { event ->
                    when(event) {
                        is OnBoardingViewModel.SnackBarEvent.NotCheckedRequiredTerm -> {
                            errorSnackBarHost.showSnackbar(getString(R.string.toast_required_terms))
                        }
                        is OnBoardingViewModel.SnackBarEvent.InvalidInviteCode -> {
                            errorSnackBarHost.showSnackbar(getString(R.string.toast_invite_invalid))
                        }
                        is OnBoardingViewModel.SnackBarEvent.ProfileNotFilled -> {
                            errorSnackBarHost.showSnackbar(getString(R.string.toast_profile_not_filled))
                        }
                    }
                }
            }
        }
    }
}