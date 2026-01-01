package com.example.planup.main.goal.viewmodel

import android.R.id.message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.planup.goal.domain.toGoalItems
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.goal.domain.GoalRepository
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {
    var fromWhere = MutableLiveData<String>()

    private val _friendState = MutableStateFlow<List<Int>>(emptyList())
    val friendState = _friendState.asStateFlow()
    val friendCnt = friendState
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ✅ UI 요청 실패 메시지 전용 (이벤트)
    private val _failMessage = MutableSharedFlow<String>()
    val failMessage = _failMessage.asSharedFlow()

    /**
     * 목표 리스트
     */
    private val _goalState = MutableStateFlow<List<GoalItem>>(emptyList())
    val goalState = _goalState.asStateFlow()

    fun fetchFriendList() = viewModelScope.launch {
        friendRepository.fetchFriendList()
            // 친구의 아이디만 적용
            .onSuccess { res -> _friendState.value = res.map { it.id } }
            .onFailWithMessage {  message ->
                _failMessage.emit(message)
                _friendState.value = emptyList()
            }
    }

    fun updateGoal(token: String, goalId: Int, request: EditGoalRequest, action: () -> Unit){
        viewModelScope.launch {
            val success = goalRepository.updateGoal(
                token = token,
                goalId = goalId,
                request = request
                )
            if (success){
                action()
            }
        }
    }

    fun fetchMyGoals(token: String?) = viewModelScope.launch {
        if (token.isNullOrEmpty()) {
            _failMessage.emit("로그인이 필요합니다.")
        }else{
            goalRepository.fetchMyGoals(token)
                .onSuccess { res -> _goalState.value = res.toGoalItems() }
                .onFailure { e ->
                    _failMessage.emit(e.message ?: "Unknown error")
                }
        }
    }

}