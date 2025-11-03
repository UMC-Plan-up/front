package com.example.planup.component.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.planup.theme.Blue200

@Composable
fun BlueSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) = SnackBarBase(hostState, modifier, Blue200)