package com.planup.planup.component.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.planup.planup.theme.Black300

@Composable
fun GraySnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) = SnackBarBase(hostState, modifier, Black300)