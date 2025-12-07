package com.example.planup.main.my.ui.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MyPagePasswordChangeViewModel @Inject constructor(

) : ViewModel() {

    val emailInput = TextFieldState()
    val passwordInput = TextFieldState()


}