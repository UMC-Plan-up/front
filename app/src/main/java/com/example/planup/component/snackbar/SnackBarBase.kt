package com.example.planup.component.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.planup.theme.Typography
import com.example.planup.theme.fontColor

@Composable
internal fun SnackBarBase(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbarColor : Color
) {
    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
        snackbar = {
            Snackbar(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                ,
                containerColor = snackbarColor,
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = it.visuals.message,
                    style = Typography.Medium_SM,
                    color = fontColor
                )
            }
        }
    )
}