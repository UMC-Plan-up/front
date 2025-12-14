package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _showAlertEmail = MutableStateFlow<String?>(null)

    val showAlertEmail = _showAlertEmail.asStateFlow()

    /**
     * 이메일 보내기
     */
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

    /**
     * 이메일 재전송
     */
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

    /**
     * Intent 검증 및 scheme 처리
     */
    fun verifyScheme(
        token: String?,
    ) {
        _showAlertEmail.update {
            null
        }

        token ?: return
        val savedEmail = verificationTokenMap.getOrDefault(token, null)
        savedEmail ?: return

        _showAlertEmail.update {
            savedEmail
        }
    }

}