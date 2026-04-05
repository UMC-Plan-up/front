package com.planup.planup.main.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.goal.item.FriendPhotoResult
import com.planup.planup.main.goal.item.GoalPhotoResult
import com.planup.planup.main.home.ui.repository.FriendPhotoRepository
import com.planup.planup.main.home.ui.repository.PhotoManageRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendPhotoViewModel @Inject constructor(
    private val repository: FriendPhotoRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val friendId = savedStateHandle["friendId"] ?: 0
    private val goalId = savedStateHandle["goalId"] ?: 0
    private val _photoList = MutableStateFlow<List<FriendPhotoResult>>(emptyList())
    val photoList: StateFlow<List<FriendPhotoResult>> = _photoList
    fun postReport(
        reason: String, block: Boolean
    ) {
        viewModelScope.launch {
            repository.postReport(friendId, reason, block)
                .onSuccess {
                    Log.d("FriendGoalDetailVM", "postReport: $it")
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailVM", "postReport fail : $it")
                }
        }
    }

    fun loadFriendPhoto(
        onCallBack: (ApiResult<List<FriendPhotoResult>>) -> Unit
    ) =
        viewModelScope.launch{
            repository.loadFriendPhotos(friendId, goalId)
                .onSuccess { result ->
                    _photoList.value = result
                    onCallBack(ApiResult.Success(result))
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailViewModel", "loadFriendPhoto: $it")
                    onCallBack(ApiResult.Fail(it))
                }
        }
}