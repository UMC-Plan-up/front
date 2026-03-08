package com.planup.planup.login.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.database.TokenSaver
import com.planup.planup.database.UserInfoSaver
import com.planup.planup.login.ui.LoginViewModel.Event.StartKakaoOnboarding
import com.planup.planup.login.ui.LoginViewModel.Event.SuccessLogin
import com.planup.planup.main.user.domain.UserRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.data.UserStatus
import com.planup.planup.network.onSuccess
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
    private val tokenSaver: TokenSaver,
    private val userInfoSaver: UserInfoSaver
) : ViewModel() {

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _snackBarEvent = Channel<SnackBarEvent>(Channel.BUFFERED)
    val snackBarEvent = _snackBarEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (tokenSaver.getToken() != null && tokenSaver.getRefreshToken() != null) {
                _eventChannel.send(SuccessLogin)
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
                    if (result.data.sanctionStatus != null) {
                        // 정지당하거나 삭제된 유저인 경우
                        with(result.data) {
                            _eventChannel.send(
                                Event.SuspendedUser(
                                    reason = sanctionReason ?: "",
                                    endDate = sanctionEndAt ?: ""
                                )
                            )
                        }
                    } else {
                        notificationRepository.updateFcmToken()
                        _eventChannel.send(Event.SuccessLogin)
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

    fun requestKakaoLogin(accessToken: String, email: String?) {
        viewModelScope.launch {
            if (email == null) {
                _eventChannel.send(Event.FailKakaoLogin)
                return@launch
            }
            userRepository.kakaoLogin(accessToken, email)
                .onSuccess {
                    when (it.userStatus) {
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

                            userInfoSaver.clearAllUserInfo()
                            userInfoSaver.saveUserInfo(it.userInfo.toUserInfoFormat())

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

        data class SuspendedUser(val reason: String, val endDate: String) : Event()
    }

    sealed class SnackBarEvent {
        object ShowExistUserSnackBar : SnackBarEvent()
    }
}