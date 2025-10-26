package com.example.planup.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun PlanUpSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val bias by animateFloatAsState(if (checked) 1f else -1f)
    val alignment by remember {
        derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) }
    }
    Box(
        modifier = modifier
            .size(
                width = 40.dp,
                height = (22.5).dp
            )
            .clip(CircleShape)
            .drawBehind {
                if (checked) {
                    drawRect(Color(0xff65A0A1))
                } else {
                    drawRect(Color(0xffd9d9d9))
                }
            }
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .padding(
                    start = 4.dp,
                    end = 4.dp
                )
                .fillMaxSize(),
            contentAlignment = alignment
        ) {
            ThumbIcon(enabled = checked, 17.dp)
        }
    }
}

@Composable
private fun ThumbIcon(
    enabled: Boolean,
    thumbSize: Dp,
) {
    val modifier = Modifier
        .size(thumbSize)
        .clip(CircleShape)
        .drawBehind {
            drawRect(Color.White)
        }
        .padding(all = 4.dp)

    Box(
        modifier = modifier
    )

}

@Composable
@Preview
private fun PlanUpSwitchPreview() {
    var checked by remember {
        mutableStateOf(false)
    }
    PlanUpSwitch(
        checked = checked,
        onCheckedChange = {
            checked = it
        }
    )
}