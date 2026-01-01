package com.example.planup.onboarding.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.theme.Blue200
import com.example.planup.theme.SemanticB6

@Composable
fun OnBoardingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 100)
) {
    val aniProgress by animateFloatAsState(
        targetValue = progress, animationSpec = animationSpec
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(SemanticB6)
            .clip(RoundedCornerShape(2.dp))
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(aniProgress)
                .background(color = Blue200)
        )
    }
}

@Preview
@Composable
private fun OnBoardingProgressBarPreview() {
    OnBoardingProgressBar(
        progress = 0.0f
    )
}