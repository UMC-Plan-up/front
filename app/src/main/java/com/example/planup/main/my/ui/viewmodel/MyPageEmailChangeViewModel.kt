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
    data object Loading : EmailChangeUiMessage
    data object EmailSendSuccess : EmailChangeUiMessage
    data object EmailReSendSuccess : EmailChangeUiMessage
    data class Error(val message: String) : EmailChangeUiMessage
}

@HiltViewModel
class MyPageEmailChangeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _emailChangeUiMessage = MutableSharedFlow<EmailChangeUiMessage>()
    val emailChangeUiMessage = _emailChangeUiMessage.asSharedFlow()

    //Token - email 매칭
    private val verificationTokenMap: MutableMap<String, String> = mutableMapOf()

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
            _emailChangeUiMessage.emit(EmailChangeUiMessage.Loading)
            userRepository.sendMailForChange(email)
                .onSuccess { emailLink ->
                    verificationTokenMap[emailLink.token] = email
                    _emailChangeUiMessage.emit(EmailChangeUiMessage.EmailSendSuccess)
                }
                .onFailWithMessageOnBlock()
        }
    }

    fun reSendEmail(
        email: String
    ) {
        viewModelScope.launch {
            _emailChangeUiMessage.emit(EmailChangeUiMessage.Loading)
            userRepository.sendMailForChange(email)
                .onSuccess { emailLink ->
                    verificationTokenMap[emailLink.token] = email
                    _emailChangeUiMessage.emit(EmailChangeUiMessage.EmailReSendSuccess)
                }
                .onFailWithMessageOnBlock()
        }
    }

    fun verifyScheme(
        token: String?,
        callbackEmail :(savedEmail : String) -> Unit
    ) {
        token ?: return
        val savedEmail = verificationTokenMap.getOrDefault(token, null)
        savedEmail ?: return
        viewModelScope.launch {

        }
    }

}