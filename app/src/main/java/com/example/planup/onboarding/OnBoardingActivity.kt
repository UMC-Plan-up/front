package com.example.planup.onboarding

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.planup.R
import com.example.planup.component.snackbar.GraySnackbarHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity: AppCompatActivity() {

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val errorSnackBarHost = remember { SnackbarHostState() }
            val navController = rememberNavController()

            Box {
                OnBoardScreen(
                    navController = navController,
                    step = state.step
                )

                GraySnackbarHost(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(bottom = 60.dp),
                    hostState = errorSnackBarHost
                )
            }

            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when(event) {
                        is OnBoardingViewModel.Event.Navigate -> {
                            navController.navigate(event.step.getRoute())
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
                    }
                }
            }
        }
    }
}