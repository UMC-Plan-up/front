package com.example.planup.main.goal.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalResult
import com.example.planup.goal.util.toGoalItems
import com.example.planup.goal.util.toGoalItemsForFriendAchieve
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.goal.domain.GoalRepository
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.main.goal.item.FriendGoalListResult
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.home.adapter.FriendGoalWithAchievement
import com.example.planup.main.home.ui.FriendGoalListRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val goalRepository: GoalRepository,
    private val friendListRepository: FriendGoalListRepository
) : ViewModel() {
    var fromWhere = MutableLiveData<String>()
    private var _targetUserId = MutableLiveData(-1)
    val targetUserId: MutableLiveData<Int>
        get() = _targetUserId

    private var _userName = MutableLiveData("사용자")
    val userName: String
        get() = _userName.value ?: "사용자"

    fun setUserName(nickname: String) {
        _userName.value = nickname
    }

    private val _friendGoals = MutableStateFlow<List<FriendGoalWithAchievement>>(emptyList())
    val friendGoals: StateFlow<List<FriendGoalWithAchievement>> = _friendGoals


    fun setFriendNickname(nickname: String) {
        _friendNickname.value = nickname
    }

    private var _goalData = MutableLiveData<EditGoalResponse>()
    val goalData: EditGoalResponse?
        get() = _goalData.value

    private var _friendNickname = MutableLiveData<String>()
    val friendNickname: String
        get() = _friendNickname.value?: "사용자"

    // ✅ UI 요청 실패 메시지 전용 (이벤트)
    private val _failMessage = MutableSharedFlow<String>()
    val failMessage = _failMessage.asSharedFlow()

    /**
     * 목표 리스트
     */
    private val _goalState = MutableStateFlow<List<GoalItem>>(emptyList())
    val goalState = _goalState.asStateFlow()

    fun setTargetUserId(userId: Int) {
        _targetUserId.value = userId
    }

    fun updateGoal(token: String, goalId: Int, request: EditGoalRequest, action: () -> Unit){
        viewModelScope.launch {
            val success = goalRepository.updateGoal(
                goalId = goalId,
                request = request
            )
            if (success){
                action()
            }
        }
    }

    fun fetchMyGoals() = viewModelScope.launch {
        goalRepository.fetchMyGoals()
            .onSuccess { res -> _goalState.value = res.toGoalItems() }
            .onFailWithMessage {
                _failMessage.emit(it)
            }
    }

    fun loadFriendGoals(
        friendId: Int,
        onCallBack: (result: ApiResult<List<GoalItem>>) -> Unit
    ) {
        viewModelScope.launch {
            val resultList = mutableListOf<FriendGoalWithAchievement>()
            try {
                val response = friendListRepository.getFriendGoalList(friendId)
                if (response is ApiResult.Success) {
                    val goals = response.data
                    for(goal in goals) {
                        val achieveRes = friendListRepository.getFriendGoalAchievement(friendId, goal)
                        if(achieveRes is ApiResult.Success) {
                            resultList.add(
                                FriendGoalWithAchievement(
                                    goalId = goal.goalId,
                                    goalName = goal.goalName,
                                    goalType = goal.goalType,
                                    goalAmount = goal.goalAmount,
                                    verificationType = goal.verificationType,
                                    goalTime = goal.goalTime,
                                    frequency = goal.frequency,
                                    oneDose = goal.oneDose,
                                    totalAchievement = achieveRes.data.totalAchievement
                                )
                            )
                            onCallBack(ApiResult.Success(resultList.toGoalItemsForFriendAchieve()))
                        }
                    }

                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }

            _friendGoals.value = resultList
        }
    }

    fun loadGoalList(friendAction: () -> Unit, action: () -> Unit) {
        viewModelScope.launch {
            if (targetUserId.value>0){
                friendAction()
                loadFriendGoals(targetUserId.value) {
                    if (it is ApiResult.Success) {
                        _goalState.value = it.data
                    }
                }
            }else{
                action()
            }
        }
    }

    fun loadTodayAchievement(action: (Int) -> Unit) {
        viewModelScope.launch {
            friendListRepository.getTodayAchievement().onSuccess {
                action(it.achievementRate)
            }.onFailWithMessage {

            }
        }
    }

    fun loadFriendGoalList(friendUserId: Int, action: (List<FriendGoalListResult>) -> Unit) {
        viewModelScope.launch {
            friendListRepository.getFriendGoalList(friendUserId).onSuccess {
                action(it)
            }.onFailWithMessage {

            }
        }
    }

    fun createGoal(goalCreateRequest: GoalCreateRequest, action: (GoalResult) -> Unit, message: (String)-> Unit){
        viewModelScope.launch {
            goalRepository.createGoal(goalCreateRequest)
                .onSuccess {
                    action(it)
                }
                .onFailWithMessage {
                    message(it)
                }
        }
    }

    fun deleteGoal(goalId: Int, action: (Int) -> Unit,message: (String)-> Unit){
        viewModelScope.launch {
            goalRepository.deleteGoal(goalId = goalId)
                .onSuccess {
                    action(goalId)
                }
                .onFailWithMessage {
                    message(it)
                }
        }
    }

    fun setGoalActive(goalId: Int, action: (Int) -> Unit, message: (String) -> Unit){
        viewModelScope.launch {
            goalRepository.setGoalActive(goalId)
                .onSuccess {
                    action(goalId)
                }
                .onFailWithMessage { message->
                    message(message)
                }
        }
    }

    fun getGoalEditData(goalId: Int,goalDataAction: (EditGoalResponse) -> Unit, backAction: (String) -> Unit) {
        viewModelScope.launch {
            goalRepository.getGoalDetail(goalId)
                .onSuccess {
                    goalDataAction(it)
                    _goalData.value = it
                }
                .onFailWithMessage { message->
                    _failMessage.emit(message)
                    backAction("목표의 데이터가 없습니다.")
                }
        }
    }
}