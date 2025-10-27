package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
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
     * 유저 닉네임 정보
     */
    private var _nickName = MutableStateFlow("")
    val nickName = _nickName.asStateFlow()

    fun fetchUserInfo() {
        viewModelScope.launch {
            fetchEmail()
            fetchNickName()
            fetchProfileImage()
            fetchNotificationLocal()
        }
    }

    suspend fun fetchEmail() = _email.update {
        userRepository.getUserEmail()
    }

    suspend fun fetchNickName() = _nickName.update {
        userRepository.getUserNickName()
    }

    suspend fun fetchProfileImage() = _profileImage.update {
        userRepository.getUserProfileImage()
    }


    suspend fun fetchNotificationLocal() = _notificationLocal.update {
        userRepository.getUserNotificationLocal()
    }

    fun updateNotificationLocal(notificationLocal: Boolean) {
        viewModelScope.launch {
            userRepository.updateUserNotificationLocal(notificationLocal)
            fetchNotificationLocal()
        }
    }
}