package com.planup.planup.login.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.database.TokenSaver
import com.planup.planup.login.ui.LoginViewModel.Event.*
import com.planup.planup.main.user.domain.UserRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.onSuccess
import com.planup.planup.network.data.UserStatus
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val tokenSaver: TokenSaver
) : ViewModel() {

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _snackBarEvent = Channel<SnackBarEvent>(Channel.BUFFERED)
    val snackBarEvent = _snackBarEvent.receiveAsFlow()
    
    init {
        viewModelScope.launch {
            userRepository.validateToken()
                .onSuccess {
                    _eventChannel.send(SuccessLogin)
                }
                .onFailWithMessage {
                    Log.i("LoginViewModel", "자동 로그인 실패: $it")
                }
        }
    }

    fun requestLogin(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            val result = userRepository.postLogin(email = email, password = password)
            when (result) {
                is ApiResult.Success -> {
                    notificationRepository.updateFcmToken()
                    _eventChannel.send(Event.SuccessLogin)
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

    fun requestKakaoLogin(accessToken: String, email: String?) {
        viewModelScope.launch {
            if (email == null) {
                _eventChannel.send(Event.FailKakaoLogin)
                return@launch
            }
            userRepository.kakaoLogin(accessToken, email)
                .onSuccess {
                    when(it.userStatus) {
                        UserStatus.SIGNUP_REQUIRED -> {
                            _eventChannel.send(
                                StartKakaoOnboarding(
                                    tempUserId = it.tempUserId!!,
                                    email = email
                                )
                            )
                        }
                        UserStatus.ACCOUNT_CONFLICT -> {
                            _snackBarEvent.send(SnackBarEvent.ShowExistUserSnackBar)
                        }
                        UserStatus.LOGIN_SUCCESS -> {
                            tokenSaver.saveToken(it.accessToken)
                            tokenSaver.saveRefreshToken(it.refreshToken)

                            notificationRepository.updateFcmToken()
                            _eventChannel.send(Event.SuccessLogin)
                        }
                        UserStatus.SIGNUP_SUCCESS -> {
                            // 해당 없음
                        }
                    }
                }
        }
    }

    sealed class Event {
        object UnknownEmail : Event()
        object WrongPassword : Event()
        object SuccessLogin : Event()
        data class FailLogin(val message: String) : Event()
        data class StartKakaoOnboarding(val tempUserId: String, val email: String) : Event()
        object FailKakaoLogin : Event()
        object UnknownError : Event()
    }

    sealed class SnackBarEvent {
        object ShowExistUserSnackBar : SnackBarEvent()
    }
}