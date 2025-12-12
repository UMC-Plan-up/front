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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.planup.component.TopHeader

@Composable
fun OnBoardScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState
) {
    val navController = rememberNavController()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopHeader(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.navigationBars))
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.Gray),
                onBackAction = {
                    navController.popBackStack()
                },
                title = state.dummy
            )

            OnboardNavHost(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White),
                navController = navController
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