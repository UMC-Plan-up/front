package com.example.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.planup.component.TopHeader
import com.example.planup.onboarding.component.OnBoardingProgressBar

@Composable
fun OnBoardScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState
) {
    val navController = rememberNavController()

    Box(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopHeader(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.navigationBars))
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                onBackAction = {
                    navController.popBackStack()
                },
                title = state.step.title ?: ""
            )

            OnBoardingProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 26.dp,
                        vertical = 12.dp
                    ),
                progress = state.step.getFloatProgress()
            )

            OnboardNavHost(
                modifier = modifier
                    .fillMaxSize(),
                navController = navController,
                state = state
            )
        }
    }
}

@Preview
@Composable
private fun OnBoardFramePreview() {
    OnBoardScreen(
        state = OnBoardingState()
    )
}