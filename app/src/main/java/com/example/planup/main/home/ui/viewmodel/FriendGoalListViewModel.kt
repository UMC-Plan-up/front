package com.example.planup.main.home.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.home.ui.FriendGoalListFragment
import com.example.planup.main.home.ui.FriendGoalListRepository
import com.example.planup.main.home.ui.FriendGoalWithAchievement
import com.example.planup.network.ApiResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FriendGoalUiMessage {
    data class Error(val message: String) : FriendGoalUiMessage
}

@HiltViewModel
class FriendGoalListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FriendGoalListRepository
) : ViewModel() {
    val friendId: Int = savedStateHandle[FriendGoalListFragment.FRIEND_ID] ?: 0
    val friendName: String = savedStateHandle[FriendGoalListFragment.FRIEND_NAME] ?: ""
    val friendProfileImage: String? = savedStateHandle[FriendGoalListFragment.FRIEND_PROFILE_IMAGE]

    private val _friendGoalUiMessage = MutableSharedFlow<FriendGoalUiMessage>()
    val friendGoalUiMessage = _friendGoalUiMessage.asSharedFlow()

    private val _friendGoals = MutableStateFlow<List<FriendGoalWithAchievement>>(emptyList())
    val friendGoals: StateFlow<List<FriendGoalWithAchievement>> = _friendGoals.asStateFlow()

    private val _achievementRate = MutableStateFlow<Int?>(null)
    val achievementRate: StateFlow<Int?> = _achievementRate.asStateFlow()

    fun loadFriendGoals() {
        viewModelScope.launch {
            repository.getFriendGoalList(friendId)
                .onSuccess { goals ->
                    for (goal in goals) {
                        _friendGoals.update {
                            goals
                                .map {
                                    repository.getFriendGoalAchievement(friendId, goal)
                                }
                                .filter { it is ApiResult.Success }
                                .map {
                                    val achieveRes = (it as ApiResult.Success).data
                                    FriendGoalWithAchievement(
                                        goalId = goal.goalId,
                                        goalName = goal.goalName,
                                        goalType = goal.goalType,
                                        goalAmount = goal.goalAmount,
                                        verificationType = goal.verificationType,
                                        goalTime = goal.goalTime,
                                        frequency = goal.frequency,
                                        oneDose = goal.oneDose,
                                        totalAchievement = achieveRes.totalAchievement
                                    )
                                }
                        }
                    }
                }
                .onFailWithMessage { message ->
                    _friendGoalUiMessage.emit(
                        FriendGoalUiMessage.Error(message)
                    )
                }
        }
    }

    fun loadTodayAchievement() {
        viewModelScope.launch {
            repository.getTodayAchievement()
                .onSuccess { data ->
                    _achievementRate.update {
                        data.achievementRate
                    }
                }.onFailWithMessage { message ->
                    _friendGoalUiMessage.emit(
                        FriendGoalUiMessage.Error(message)
                    )
                }
        }
    }
}
