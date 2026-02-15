package com.example.planup.main.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.goal.item.GetCommentsResult
import com.example.planup.main.home.ui.repository.FriendGoalDetailRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
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
    private val _
    fun loadComment() {
        viewModelScope.launch {
            repository.loadComment(goalId)
                .onSuccess { result ->
                    _comments.value = result
                }.onFailWithMessage {
                    Log.d("FriendGoalDetailViewModel", "loadComment: $it")
                }
        }
    }

    fun loadTodayFriendTime() {
        viewModelScope.launch {
            repository.loadTodayFriendTime(goalId, friendId)
                .onSuccess { result ->

                }
    }


}