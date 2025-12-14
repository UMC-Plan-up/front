package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.data.KakaoLink
import com.example.planup.network.data.UsingKakao
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface MyPageUiState {
    data object Init : MyPageUiState

    data object Loading : MyPageUiState

    data class Error(
        val message: String
    ) : MyPageUiState

    data class SuccessNotificationMarketing(
        val newNotification: Boolean,
        val date: String
    ) : MyPageUiState

}

@HiltViewModel
class MyPageInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyPageUiState>(MyPageUiState.Init)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private var _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private var _profileImage = MutableStateFlow("")
    val profileImage = _profileImage.asStateFlow()

    /**
     * 서비스 알림 수신 정보
     */
    private var _notificationService = MutableStateFlow(false)
    val notificationService = _notificationService.asStateFlow()

    /**
     * 혜택 및 마케팅 알림 정보
     */
    private var _marketingNotification = MutableStateFlow(false)
    val marketingNotification = _marketingNotification.asStateFlow()

    /**
     * 카카오 계정 연결 정보
     */
    private var _kakaoLink = MutableStateFlow<UsingKakao>(UsingKakao(null,false))
    val kakaoLink = _kakaoLink.asStateFlow()

    /**
     * 유저 닉네임 정보
     */
    private var _nickName = MutableStateFlow("")
    val nickName = _nickName.asStateFlow()

    /**
     * 모든 정보 갱신
     *
     */
    fun fetchUserInfo() {
        viewModelScope.launch() {
            supervisorScope {
                launch {
                    fetchEmail()
                }
                launch {
                    fetchNickName()
                }
                launch {
                    fetchProfileImage()
                }
                launch {
                    fetchNotificationService()
                }
                launch {
                    fetchNotificationMarketing()
                }
                launch {
                    fetchKaKaoAccount()
                }
            }
        }
    }

    /**
     * 이메일 정보 갱신
     *
     */
    suspend fun fetchEmail() = _email.update {
        userRepository.getUserEmail()
    }

    /**
     * 닉네임 정보 갱신
     *
     */
    suspend fun fetchNickName() = _nickName.update {
        userRepository.getUserNickName()
    }

    /**
     * 프로필 이미지 정보 갱신
     *
     */
    suspend fun fetchProfileImage() = _profileImage.update {
        userRepository.getUserProfileImage()
    }

    /**
     * 서비스 알림 수신 정보 갱신
     */
    suspend fun fetchNotificationService() = _notificationService.update {
        userRepository.getUserNotificationService()
    }

    /**
     * 서비스 알림 수신 정보 갱신
     */
    suspend fun fetchNotificationMarketing() = _marketingNotification.update {
        userRepository.getUserNotificationMarketing()
    }

    suspend fun fetchKaKaoAccount() = _kakaoLink.update {
        val result = userRepository.getKakaoAccountLink()
        if (result is ApiResult.Success) {
            result.data
        } else {
            UsingKakao(null,false)
        }
    }


    /**
     * 서비스 알림 수신 정보 갱신
     *
     * @param notificationService
     */
    fun updateNotificationService(
        notificationService: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update {
                MyPageUiState.Loading
            }
            userRepository.updateUserNotificationService(notificationService)
                .onSuccess {
                    fetchNotificationService()
                }.onFailWithMessage { message ->
                    _uiState.update {
                        MyPageUiState.Error(message)
                    }
                }
        }
    }

    fun updateNotificationMarketing(
        notificationMarketing: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update {
                MyPageUiState.Loading
            }
            userRepository.updateUserNotificationMarketing(notificationMarketing)
                .onSuccess {
                    fetchNotificationMarketing()
                    _uiState.update {
                        val date =
                            SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
                        MyPageUiState.SuccessNotificationMarketing(
                            newNotification = notificationMarketing,
                            date = date
                        )
                    }
                }.onFailWithMessage { message ->
                    _uiState.update {
                        MyPageUiState.Error(message)
                    }
                }
        }
    }
}