package com.example.planup.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SnackbarEvent(
    val id: Long = System.currentTimeMillis(),
    val message: String
)

@HiltViewModel
class MainSnackbarViewModel @Inject constructor(

) : ViewModel() {

    private val _snackbarErrorEvents = MutableSharedFlow<SnackbarEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val snackbarErrorEvents = _snackbarErrorEvents.asSharedFlow()


    fun updateErrorMessage(message: String) {
        viewModelScope.launch {
            _snackbarErrorEvents.emit(SnackbarEvent(message = message))
        }
    }

    private val _snackbarBlueEvents = MutableSharedFlow<SnackbarEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val snackbarBlueEvents = _snackbarBlueEvents.asSharedFlow()

    fun updateSuccessMessage(message: String) {
        viewModelScope.launch {
            _snackbarBlueEvents.emit(SnackbarEvent(message = message))
        }
    }

}