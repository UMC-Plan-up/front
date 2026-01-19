package com.example.planup.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _inviteCode: MutableStateFlow<String> = MutableStateFlow("")
    val inviteCode: StateFlow<String> = _inviteCode

    private val _event: Channel<Event> = Channel()
    val event = _event.receiveAsFlow()

    init {
        // TODO:: 테스트 할 때 주석 풀기
//        viewModelScope.launch {
//            val code = userRepository.getInviteCode()
//            println("inviteCode = $code")
//            _inviteCode.update { code }
//        }
    }

    fun shareCodeWithSMS() {
        viewModelScope.launch {
            val nickname = userRepository.getUserNickName()
                .let { it.ifBlank { null } }

            _event.send(
                Event.SendCodeWithSMS(
                    nickname = nickname,
                    inviteCode = inviteCode.value
                )
            )
        }
    }

    fun shareCodeWithKakao() {
        viewModelScope.launch {
            val nickname = userRepository.getUserNickName()
                .let { it.ifBlank { null } }

            _event.send(
                Event.SendCodeWithKakao(
                    nickname = nickname,
                    inviteCode = inviteCode.value
                )
            )
        }
    }

    fun sendFriendRequest(input: String) {
        viewModelScope.launch {
            userRepository.validateInviteCode(input)
                .onSuccess {
                    _event.send(Event.AcceptFriendRequest)
                }
                .onFailWithMessage {
                    // 네트워크 오류는 어떻게 표시?
                    _event.send(Event.InvalidInviteCode)
                }
        }
    }

    sealed class Event {
        data object InvalidInviteCode : Event()
        data object AcceptFriendRequest : Event()
        data class SendCodeWithSMS(val nickname: String?, val inviteCode: String) : Event()
        data class SendCodeWithKakao(val nickname: String?, val inviteCode: String) : Event()
    }
}