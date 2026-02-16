package com.planup.planup.main.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.goal.item.CreateCommentRequest
import com.planup.planup.main.goal.item.FriendPhotoResult
import com.planup.planup.main.goal.item.GetCommentsResult
import com.planup.planup.main.home.ui.repository.FriendGoalDetailRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class FriendGoalDetailViewModel @Inject constructor(
    private val repository: FriendGoalDetailRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val goalId = savedStateHandle["goalId"] ?: 0
    val title = savedStateHandle["title"] ?: ""
    val friendId = savedStateHandle["friendId"] ?: 0

    private val _comments = MutableStateFlow<List<GetCommentsResult>>(emptyList())
    val comments: StateFlow<List<GetCommentsResult>> = _comments
    private val _todayTime = MutableStateFlow("00:00:00")
    val todayTime: StateFlow<String> = _todayTime
    private val _friendPhotos = MutableStateFlow<List<FriendPhotoResult>>(emptyList())
    val friendPhotos: StateFlow<List<FriendPhotoResult>> = _friendPhotos


    fun loadComment(
        onCallBack: (result: ApiResult<List<GetCommentsResult>>) -> Unit
    ) {
        viewModelScope.launch {
            repository.loadComment(goalId)
                .onSuccess { result ->
                    _comments.value = result
                    onCallBack(ApiResult.Success(result))
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailViewModel", "loadComment: $it")
                    onCallBack(ApiResult.Fail(it))
                }
        }
    }

    fun loadTodayFriendTime(
        onCallBack: (result: ApiResult<String>) -> Unit
    ) =
        viewModelScope.launch {
            repository.loadTodayFriendTime(goalId, friendId)
                .onSuccess { result ->
                    _todayTime.value = result.formattedTime
                    onCallBack(ApiResult.Success(result.formattedTime))
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailViewModel", "loadTodayFriendTime: $it")
                    onCallBack(ApiResult.Fail(it))
                }
    }

    fun loadFriendPhoto(
        onCallBack: (ApiResult<List<FriendPhotoResult>>) -> Unit
    ) =
        viewModelScope.launch{
            repository.loadFriendPhotos(friendId, goalId)
                .onSuccess { result ->
                    _friendPhotos.value = result
                    onCallBack(ApiResult.Success(result))
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailViewModel", "loadFriendPhoto: $it")
                    onCallBack(ApiResult.Fail(it))
                }
        }

    fun sendComment(
        comment: String,
        onCallBack: (ApiResult<String>) -> Unit
    ) {
        viewModelScope.launch {
            val content = CreateCommentRequest(
                content = comment,
                parentCommentId = 0,
                reply = false
            )
            repository.sendComment(goalId, content)
                .onSuccess { result ->
                    Log.d("FriendGoalDetailViewModel", "sendComment: $result")
                    onCallBack(ApiResult.Success(result.content))
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailViewModel", "sendComment: $it")
                    onCallBack(ApiResult.Fail(it))
                }

        }
    }

    fun sendReaction(
        isEncourage: Boolean
    ) {
        viewModelScope.launch {
            if(isEncourage) {
                repository.sendEncourage(goalId)
                    .onSuccess { result ->
                        Log.d("FriendGoalDetailViewModel", "sendReaction: $result")
                    }.onFailWithMessage {
                        Log.d("FriendGoalDetailViewModel", "sendReaction: $it")
                    }
            } else {
                repository.sendCheer(goalId)
                    .onSuccess {
                        Log.d("FriendGoalDetailViewModel", "sendReaction: $it")
                    }.onFailWithMessage {
                        Log.d("FriendGoalDetailViewModel", "sendReaction: $it")
                    }
            }

        }
    }
}