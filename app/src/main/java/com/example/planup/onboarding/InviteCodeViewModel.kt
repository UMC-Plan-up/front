package com.example.planup.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
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

            _event.send(
                Event.SendCodeWithSMS(
                    nickname = nickname,
                    inviteCode = inviteCode.value
                )
            )
        }
    }

    fun shareCodeWithKakao() {

    }

    sealed class Event {
        data class SendCodeWithSMS(val nickname: String?, val inviteCode: String) : Event()
    }
}