package com.example.planup.main.friend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.network.ApiResult
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.network.dto.friend.FriendRequestsResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FriendUiMessage {
    data class ReportSuccess(val friendName: String) : FriendUiMessage()
    data class Error(val msg: String) : FriendUiMessage()
}

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private var _friendList = MutableStateFlow(listOf<FriendInfo>())
    val friendList = _friendList.asStateFlow()

    private var _friendRequestList = MutableStateFlow(listOf<FriendRequestsResult>())
    val friendRequestList = _friendRequestList.asStateFlow()


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
    fun fetchFriendList(
        onCallBack: (result: ApiResult<List<FriendInfo>>) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val friendListResult = friendRepository.getFriendList()
                if (friendListResult is ApiResult.Success) {
                    _friendList.value = friendListResult.data
                }
                onCallBack(friendListResult)
            } catch (e: CancellationException) {
                //코루틴의 취소 요청은 정상 신호 이므로 잡으면 안됨
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                //그외 에러는 잡아야됨.
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun fetchFriendRequest(
        onCallBack: (result: ApiResult<List<FriendRequestsResult>>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val friendRequestResult = friendRepository.getFriendRequestList()

                if (friendRequestResult is ApiResult.Success) {
                    _friendRequestList.value = friendRequestResult.data
                } else {
                    _friendRequestList.value = emptyList()
                }
                onCallBack(friendRequestResult)
            } catch (e: CancellationException) {
                //코루틴의 취소 요청은 정상 신호 이므로 잡으면 안됨
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                //그외 에러는 잡아야됨.
                onCallBack(ApiResult.Exception(e))
            }
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
                            FriendUiMessage.ReportSuccess(friend.name)
                        )
                    } else {
                        _uiMessage.emit(
                            FriendUiMessage.Error("Fail Report")
                        )
                    }
                }
                .onFailWithMessageOnBlock()
        }
    }
}