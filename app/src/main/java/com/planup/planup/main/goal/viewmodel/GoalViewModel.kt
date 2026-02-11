package com.example.planup.main.goal.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalResult
import com.example.planup.goal.util.TmpGoalData
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
    private val friendListRepository: FriendGoalListRepository,
) : ViewModel() {
    var fromWhere = MutableLiveData<String>()

    private var _isCreateGoal = MutableStateFlow(false)
    val isCreateGoal: StateFlow<Boolean>
        get() = _isCreateGoal.asStateFlow()
    private var _isGoalLevel = MutableStateFlow(10)
    val isGoalLevel: Int
        get() = _isGoalLevel.value

    private var _targetUserId = MutableLiveData(-1)
    val targetUserId: MutableLiveData<Int>
        get() = _targetUserId

    private var _userName = MutableStateFlow("사용자")
    val userName: String
        get() = _userName.value

    fun setUserName(nickname: String) {
        _userName.value = nickname
    }

    private val _friendGoals = MutableStateFlow<List<FriendGoalWithAchievement>>(emptyList())
    val friendGoals: StateFlow<List<FriendGoalWithAchievement>> = _friendGoals


    fun setFriendNickname(nickname: String) {
        _friendNickname.value = nickname
    }
    private var _goalId = MutableLiveData<Int>()
    val goalId: Int
        get() = _goalId.value ?: -1
    private var _editGoalData = MutableLiveData<EditGoalResponse>()
    val editGoalData: EditGoalResponse?
        get() = _editGoalData.value

    private var _goalData = MutableLiveData<TmpGoalData>()
    val goalData: TmpGoalData
        get() = _goalData.value ?: TmpGoalData()

    fun setGoalData(goalData: TmpGoalData){
        _goalData.value = goalData
    }
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

    fun setGoalState(goalState: List<GoalItem>){
        _goalState.value = goalState
    }

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
            .onSuccess { res ->
                _goalState.value = res.toGoalItems()
            }
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
                    _editGoalData.value = it
                    _goalId.value = goalId
                }
                .onFailWithMessage { message->
                    _failMessage.emit(message)
                    backAction("목표의 데이터가 없습니다.")
                }
        }
    }

    fun joinGoal(goalId: Int, action: (Int) -> Unit, message: (String) -> Unit){
        viewModelScope.launch {
            goalRepository.joinGoal(goalId)
                .onSuccess {
                    action(goalId)
                }.onFailWithMessage {
                    _failMessage.emit(it)
                    message(it)
                }
        }

    }

    fun getGoalLevel(action: (Int) -> Unit) = viewModelScope.launch {
        goalRepository.getGoalLevel()
            .onSuccess {
                _isGoalLevel.value = it.split("_")[1].toInt()
                Log.d("getGoalLevel","_goalState : ${_goalState.value.size}")
                _isCreateGoal.value = _goalState.value.size >= _isGoalLevel.value
                action(_isGoalLevel.value)
            }
            .onFailWithMessage {
                Log.d("getGoalLevel","error : $it")
                _failMessage.emit(it)
            }
    }

}