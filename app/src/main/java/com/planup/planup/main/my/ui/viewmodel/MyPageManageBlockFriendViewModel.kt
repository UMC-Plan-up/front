package com.planup.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.friend.domain.FriendRepository
import com.planup.planup.main.my.data.BlockedFriend
import com.planup.planup.network.ApiResult
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiMessage {
    data class UnBlockSuccess(val friendName: String) : UiMessage()
    data class ReportSuccess(val friendName: String) : UiMessage()
    data class Error(val msg: String) : UiMessage()
}


@HiltViewModel
class MyPageManageBlockFriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    val blockFriendList = friendRepository.getFriendBlockList().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _uiMessage = MutableSharedFlow<UiMessage>()
    val uiMessage = _uiMessage.asSharedFlow()

    private suspend fun <T> ApiResult<T>.onFailWithMessageOnBlock() {
        this.onFailWithMessage { message ->
            _uiMessage.emit(
                UiMessage.Error(message)
            )
        }
    }

    fun fetchBlockFriend() {
        viewModelScope.launch {
            friendRepository
                .fetchFriendBlockList()
                .onFailWithMessageOnBlock()
        }
    }

    fun unBlockFriend(
        friend: BlockedFriend
    ) {
        viewModelScope.launch {
            friendRepository
                .unBlockFriend(
                    friendId = friend.id
                )
                .onSuccess { result ->
                    if (result) {
                        _uiMessage.emit(
                            UiMessage.UnBlockSuccess(friend.name)
                        )
                    } else {
                        _uiMessage.emit(
                            UiMessage.Error("Fail UnBlock")
                        )
                    }
                }
                .onFailWithMessageOnBlock()
        }
    }

    fun reportFriend(
        friend: BlockedFriend,
        reason: String,
        withBlock: Boolean
    ) {
        viewModelScope.launch {
            friendRepository
                .reportFriend(
                    friendId = friend.id,
                    reason = reason,
                    withBlock = withBlock
                )
                .onSuccess { result ->
                    _uiMessage.emit(
                        UiMessage.ReportSuccess(friend.name)
                    )
                }
                .onFailWithMessageOnBlock()
        }
    }
}