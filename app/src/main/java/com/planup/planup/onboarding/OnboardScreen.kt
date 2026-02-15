package com.planup.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.planup.planup.component.TopHeader
import com.planup.planup.onboarding.component.OnBoardingProgressBar
import com.planup.planup.onboarding.model.OnboardingStep

@Composable
fun OnBoardScreen(
    step: OnboardingStep,
    onBackAction: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

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
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                onBackAction = onBackAction,
                title = step.title ?: ""
            )

            OnBoardingProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 26.dp,
                        vertical = 12.dp
                    ),
                progress = step.getFloatProgress()
            )

            OnboardNavHost(
                modifier = modifier
                    .fillMaxSize(),
                currentStep = step,
                navController = navController
            )
        }
    }
}

@Preview
@Composable
private fun OnBoardFramePreview() {
    OnBoardScreen(
        step = OnboardingStep.Term,
        onBackAction = {}
    )
}