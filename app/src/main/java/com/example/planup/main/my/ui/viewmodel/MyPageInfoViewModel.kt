package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private var _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private var _profileImage = MutableStateFlow("")
    val profileImage = _profileImage.asStateFlow()

    /**
     * 서비스 알림 수신 정보
     */
    private var _notificationLocal = MutableStateFlow(false)
    val notificationLocal = _notificationLocal.asStateFlow()

    /**
     * 혜택 및 마케팅 알림 정보
     */
    private var _marketingNotification = MutableStateFlow(false)
    val marketingNotification = _marketingNotification.asStateFlow()

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
        viewModelScope.launch {
            fetchEmail()
            fetchNickName()
            fetchProfileImage()
            fetchNotificationLocal()
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
    suspend fun fetchNotificationLocal() = _notificationLocal.update {
        userRepository.getUserNotificationLocal()
    }

    /**
     * 서비스 알림 수신 정보 갱신
     */
    suspend fun fetchNotificationMarketing() = _notificationLocal.update {
        userRepository.getUserNotificationMarketing()
    }


    /**
     * 서비스 알림 수신 정보 갱신
     *
     * @param notificationLocal
     */
    fun updateNotificationLocal(notificationLocal: Boolean) {
        viewModelScope.launch {
            userRepository.updateUserNotificationLocal(notificationLocal)
            fetchNotificationLocal()
        }
    }

    fun updateNotificationMarketing(
        notificationMarketing: Boolean,
        onSuccess :() -> Unit,
        onFail :(message: String) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.updateUserNotificationMarketing(notificationMarketing)
                .onSuccess {
                    fetchNotificationLocal()
                    onSuccess()
                }.onFailWithMessage(onFail)
        }
    }
}