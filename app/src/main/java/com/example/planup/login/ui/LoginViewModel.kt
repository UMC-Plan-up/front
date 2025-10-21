package com.example.planup.login.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _eventChannel =  Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    fun requestLogin(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            val result = userRepository.postLogin(email = email, password = password)
            when(result) {
                is ApiResult.Success -> {
                    when(result.data.message) {
                        IS_UNKNOWN_EMAIL -> _eventChannel.send(Event.UnknownEmail)
                        IS_WRONG_PASSWORD -> _eventChannel.send(Event.WrongPassword)
                        else -> _eventChannel.send(Event.SuccessLogin)
                    }
                }
                is ApiResult.Error -> _eventChannel.send(Event.FailLogin(result.message))
                is ApiResult.Exception -> {
                    Log.e("LoginViewModel", "Fail requestLogin. exception: ${result.error}")
                    _eventChannel.send(Event.UnknownError)
                }
                is ApiResult.Fail -> _eventChannel.send(Event.FailLogin(result.message))
            }
        }
    }




    sealed class Event {
        object UnknownEmail: Event()
        object WrongPassword: Event()
        object SuccessLogin: Event()
        data class FailLogin(val message: String): Event()
        object UnknownError: Event()
    }

    companion object {
        private const val IS_UNKNOWN_EMAIL = "존재하지 않는 사용자입니다"
        private const val IS_WRONG_PASSWORD = "비밀번호가 일치하지 않습니다"
    }
}