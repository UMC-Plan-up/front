package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EmailChangeUiMessage {
    data object EmailSendSuccess : EmailChangeUiMessage
    data class Error(val message: String) : EmailChangeUiMessage
}

@HiltViewModel
class MyPageEmailChangeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private var _emailChangeUiMessage = MutableSharedFlow<EmailChangeUiMessage>()
    val emailChangeUiMessage = _emailChangeUiMessage.asSharedFlow()

    private suspend fun <T> ApiResult<T>.onFailWithMessageOnBlock() {
        this.onFailWithMessage { message ->
            _emailChangeUiMessage.emit(
                EmailChangeUiMessage.Error(message)
            )
        }
    }

    fun sendEmail(
        email: String
    ) {
        viewModelScope.launch {
            userRepository.sendMail(email)
                .onSuccess {
                    _emailChangeUiMessage.emit(EmailChangeUiMessage.EmailSendSuccess)
                }
                .onFailWithMessageOnBlock()
        }
    }
}