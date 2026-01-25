package com.example.planup.onboarding

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
                            startActivity(Intent(this@OnBoardingActivity, InviteCodeActivity::class.java))
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
                        is OnBoardingViewModel.Event.FinishSignup -> {
                            isFinishDialogVisible = true
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.snackBarEvent.collect { event ->
                    when(event) {
                        is OnBoardingViewModel.SnackBarEvent.UndefinedError -> {
                            errorSnackBarHost.showSnackbar(event.message)
                        }
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

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        setIntent(intent)

        val email = intent.getStringExtra(QUERY_EMAIL) ?: ""
        val verified = intent.getStringExtra(QUERY_VERIFIED) ?: ""
        val token = intent.getStringExtra(QUERY_TOKEN) ?: ""

        if(email.isNotEmpty() && token.isNotEmpty() && verified == "true")
            viewModel.validateDeeplink(email, token)
    }

    companion object {
        private const val QUERY_EMAIL = "email"
        private const val QUERY_VERIFIED = "verified"
        private const val QUERY_TOKEN = "token"

        fun getIntent(context: Context, deeplink: Uri): Intent {
            val parameters = getQueryParameters(deeplink)

            val intent = Intent(context, OnBoardingActivity::class.java)
                .apply {
                    parameters.forEach { key, value ->
                        putExtra(key, value)
                    }
                    addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                }
            return intent
        }

        private fun getQueryParameters(deeplink: Uri): Map<String, String> {
            val parameters = deeplink.queryParameterNames.also {
                // 딥링크에 요구하는 파라미터가 다 들어왔는지 확인
                if(!it.contains(QUERY_EMAIL) || !it.contains(QUERY_VERIFIED) || !it.contains(QUERY_TOKEN)) {
                    Log.e("OnBoardingActivity", "Deeplink parameter is missing $deeplink")
                }
            }.associateWith { name ->
                // 데이터 추출
                deeplink.getQueryParameter(name).let {
                    if (it.isNullOrBlank()) {
                        Log.e("OnBoardingActivity", "Deeplink parameter is empty: $name")
                        ""
                    } else {
                        it
                    }
                }
            }

            return parameters
        }
    }
}