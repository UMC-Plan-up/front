package com.planup.planup.component.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.planup.planup.theme.Blue200

@Composable
fun BlueSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) = SnackBarBase(hostState, modifier, Blue200)