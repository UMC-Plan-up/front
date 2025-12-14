package com.example.planup.main.user.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import com.example.planup.signup.data.ProcessResult
import com.example.planup.util.ShareTool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInviteCodeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val shareTool: ShareTool
) : ViewModel() {

    private var _inviteCode = MutableStateFlow("")
    val inviteCode = _inviteCode.asStateFlow()

    fun fetchMyInviteCode(
        onCallBack: (inviteCode: String) -> Unit
    ) {
        if (_inviteCode.value.isEmpty()) {
            //초대 코드가 없을 경우 에만
            viewModelScope.launch {
                _inviteCode.update {
                    userRepository.getInviteCode()
                }
                onCallBack(_inviteCode.value)
            }
        }
    }

    fun fetchInvalidateInviteCode(
        code: String,
        onCallBack: (ProcessResult) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.validateInviteCode(code)
                .onSuccess(onCallBack)
                .onFailWithMessage(onError)
        }
    }

    fun copyToClipboard() {
        shareTool.copyInviteCodeToClipboard(_inviteCode.value)
    }

    fun shareToSMS() {
        shareTool.shareInviteCodeToSMS(_inviteCode.value)
    }

    fun shareEtc() {
        shareTool.shareText(_inviteCode.value)
    }

    fun shareKaKao(
        activityContext: Activity
    ) {
        shareTool.shareToKakao(activityContext, _inviteCode.value)
    }
}