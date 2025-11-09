package com.example.planup.main.friend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FriendUiMessage {
    data class DeleteSuccess(val friendName: String) : FriendUiMessage()
    data class BlockSuccess(val friendName: String) : FriendUiMessage()
    data class ReportSuccess(val friendName: String) : FriendUiMessage()
    data class Error(val msg: String) : FriendUiMessage()
}

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    val friendList = friendRepository
        .getFriendList().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val friendRequestList = friendRepository
        .getFriendBlockList().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    private val _uiMessage = MutableSharedFlow<FriendUiMessage>()
    val uiMessage = _uiMessage.asSharedFlow()

    private suspend fun <T> ApiResult<T>.onFailWithMessageOnBlock() {
        this.onFailWithMessage { message ->
            _uiMessage.emit(
                FriendUiMessage.Error(message)
            )
        }
    }

    /**
     * 요청한다.
     * 보통 해당 방식 보다는.. sealedClass를 통해 통신을 하면 더 좋음..
     */
    fun fetchFriendList() {
        viewModelScope.launch {
            friendRepository.fetchFriendList()
                .onFailWithMessageOnBlock()
        }
    }

    fun fetchFriendRequest() {
        viewModelScope.launch {
            friendRepository.fetchFriendBlockList()
                .onFailWithMessageOnBlock()
        }
    }

    fun reportFriend(
        friend: FriendInfo,
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
                        FriendUiMessage.ReportSuccess(friend.nickname)
                    )
                }
                .onFailWithMessageOnBlock()
        }
    }

    fun blockFriend(
        friend: FriendInfo
    ) {
        viewModelScope.launch {
            friendRepository
                .blockFriend(
                    friendId = friend.id
                )
                .onSuccess { result ->
                    _uiMessage.emit(
                        FriendUiMessage.BlockSuccess(friend.nickname)
                    )
                }.onFailWithMessageOnBlock()
        }
    }

    fun deleteFriend(
        friend: FriendInfo
    ) {
        viewModelScope.launch {
            friendRepository
                .deleteFriend(
                    friendId = friend.id
                )
                .onSuccess { result ->
                    _uiMessage.emit(
                        FriendUiMessage.DeleteSuccess(friend.nickname)
                    )
                }.onFailWithMessageOnBlock()
        }
    }
}