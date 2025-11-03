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

    private val _snackbarEvents = MutableSharedFlow<SnackbarEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val snackbarEvents = _snackbarEvents.asSharedFlow()

    fun updateMessage(message: String) {
        viewModelScope.launch {
            _snackbarEvents.emit(SnackbarEvent(message = message))
        }
    }
}