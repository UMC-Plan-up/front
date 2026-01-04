package com.example.planup.main.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.planup.main.goal.item.DailyAchievementResult
import com.example.planup.main.home.data.FriendGoalListRepository
import com.example.planup.main.home.ui.FriendGoalWithAchievement
import com.example.planup.network.ApiResult
import com.example.planup.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FriendGoalListViewModel @Inject constructor(
    private val repository: FriendGoalListRepository
) : ViewModel() {

    private val _friendGoals = MutableStateFlow<List<FriendGoalWithAchievement>>(emptyList())
    val friendGoals: StateFlow<List<FriendGoalWithAchievement>> = _friendGoals

    private val _achievementRate = MutableStateFlow<Int?>(null)
    val achievementRate: StateFlow<Int?> = _achievementRate

    private val sampleItems = mutableListOf(
        FriendGoalWithAchievement(
            goalId = 1,
            goalName = "헬스장 가기",
            goalType = "FRIEND",
            goalAmount = "헬스장 가서 30분 채우고 오기",
            verificationType = "TIMER",
            goalTime = 400,
            frequency = 1,
            oneDose = 1,
            totalAchievement = 85
        ),
        FriendGoalWithAchievement(
            goalId = 2,
            goalName = "물 마시기",
            goalType = "FRIEND",
            goalAmount = "벌컥벌컥~",
            verificationType = "PHOTO",
            goalTime = 400,
            frequency = 1,
            oneDose = 1,
            totalAchievement = 70
        )
    )

    fun loadFriendGoals(
        friendId: Int,
        onCallBack: (result: ApiResult<List<FriendGoalWithAchievement>>) -> Unit
    ) {
        viewModelScope.launch {
            val resultList = mutableListOf<FriendGoalWithAchievement>()
            try {
                val response = repository.getFriendGoalList(friendId)
                if (response is ApiResult.Success) {
                    val goals = response.data
                    for(goal in goals) {
                        val achieveRes = repository.getFriendGoalAchievement(friendId, goal)
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
                            onCallBack(ApiResult.Success(resultList))
                        }
                    }

                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
            //더미
            resultList.add(sampleItems[0])
            resultList.add(sampleItems[1])
            resultList.add(sampleItems[1])
            resultList.add(sampleItems[1])

            _friendGoals.value = resultList
        }
    }

    fun loadTodayAchievement(
        onCallBack: (result: ApiResult<DailyAchievementResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getTodayAchievement()
                if (response is ApiResult.Success) {
                    _achievementRate.value = response.data.achievementRate
                }
                onCallBack(response)
            } catch(e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is HttpException) {
                    Log.e("todayachievement", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
                } else {
                    Log.e("todayachievement", "Other error: ${e.message}", e)
                }
                onCallBack(ApiResult.Exception(e))
            }
        }
    }
}
