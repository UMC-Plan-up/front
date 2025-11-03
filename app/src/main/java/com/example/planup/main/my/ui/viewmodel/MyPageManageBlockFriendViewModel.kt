package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.network.ApiResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private var _blockFriendList = MutableStateFlow(emptyList<BlockedFriend>())
    val blockFriendList = _blockFriendList.asStateFlow()

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
                .getFriendBlockList()
                .onSuccess { result ->
                    _blockFriendList.update {
                        result
                    }
                }
                .onFailWithMessageOnBlock()
        }
    }

    fun unBlockFriend(
        friend: BlockedFriend
    ) {
        viewModelScope.launch {
            friendRepository
                .unBlockFriend(
                    friendName = friend.name
                )
                .onSuccess { result ->
                    if (result) {
                        //성공 했다면 리스트에서 제거 시도
                        _blockFriendList.update { current ->
                            current.filterNot { blocked ->
                                blocked.id == friend.id
                            }
                        }
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
                    if (result) {
                        _uiMessage.emit(
                            UiMessage.ReportSuccess(friend.name)
                        )
                    } else {
                        _uiMessage.emit(
                            UiMessage.Error("Fail Report")
                        )
                    }
                }
                .onFailWithMessageOnBlock()
        }
    }
}