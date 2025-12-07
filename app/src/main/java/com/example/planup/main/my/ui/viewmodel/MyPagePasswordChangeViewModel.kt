package com.example.planup.main.my.ui.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MyPagePasswordEvent {
    data object SuccessLogin : MyPagePasswordEvent
    data class Error(val message: String) : MyPagePasswordEvent
}

@HiltViewModel
class MyPagePasswordChangeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val emailInput = TextFieldState()
    val passwordInput = TextFieldState()

    private val _uiEvent = MutableSharedFlow<MyPagePasswordEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun login(

    ) {
        viewModelScope.launch {
            val email = emailInput.text.toString()
            if (email != userRepository.getUserEmail()) {
                _uiEvent.emit(MyPagePasswordEvent.Error("본인의 이메일 주소를 확인해주세요."))
                return@launch
            }
            userRepository
                .postLogin(
                    email = email,
                    password = passwordInput.text.toString()
                )
                .onSuccess {
                    _uiEvent.emit(MyPagePasswordEvent.SuccessLogin)
                }
                .onFailWithMessage { message ->
                    _uiEvent.emit(MyPagePasswordEvent.Error(message))
                }
        }
    }
}