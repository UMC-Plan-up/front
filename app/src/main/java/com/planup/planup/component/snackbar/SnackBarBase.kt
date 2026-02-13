package com.planup.planup.component.snackbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.planup.planup.theme.Typography
import com.planup.planup.theme.fontColor
import com.planup.planup.R
import com.planup.planup.theme.Accent3

@Composable
internal fun SnackBarBase(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbarColor : Color,
    actionTextColor: Color = Accent3
) {
    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
        snackbar = {
            Snackbar(
                modifier = Modifier,
                containerColor = snackbarColor,
                dismissAction = {
                    Text(
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 20.dp
                            )
                            .clickable {
                                it.dismiss()
                            },
                        text = stringResource(R.string.btn_close),
                        style = Typography.Medium_SM,
                        color = actionTextColor
                    )
                },
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