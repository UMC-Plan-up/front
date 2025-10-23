package com.example.planup.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainSnackbarViewModel @Inject constructor(

) : ViewModel() {

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    fun updateMessage(message: String) {
        snackbarMessage = message
    }

    fun clearMessage() {
        snackbarMessage = null
    }
}