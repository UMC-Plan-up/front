package com.example.planup.main.home.data

import android.util.Log
import com.example.planup.database.TokenSaver
import com.example.planup.database.checkToken
import com.example.planup.main.goal.item.DailyAchievementResult
import com.example.planup.main.goal.item.FriendGoalAchievementResult
import com.example.planup.main.goal.item.FriendGoalListResult
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.home.ui.FriendGoalWithAchievement
import com.example.planup.network.ApiResult
import com.example.planup.network.GoalApi
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.RetrofitInstance.goalApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendGoalListRepository @Inject constructor(
    private val goalApi: GoalApi,
) {
    suspend fun getFriendGoalList(friendId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getFriendGoalList(friendId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    suspend fun getFriendGoalAchievement(
        friendId: Int,
        goal: FriendGoalListResult
    ): ApiResult<FriendGoalAchievementResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getFriendGoalAchievement(friendId, goal.goalId)
                },
                onResponse = { response ->
                    if(response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }
//    suspend fun getFriendGoalAchievements(
//        token: String,
//        friendId: Int,
//        goals: List<FriendGoalListResult>
//    ): List<FriendGoalWithAchievement> {
//        val resultList = mutableListOf<FriendGoalWithAchievement>()
//        for (goal in goals) {
//            try {
//                val response = goalApi.getFriendGoalAchievement("Bearer $token", friendId, goal.goalId)
//                if (response.isSuccess) {
//                    resultList.add(
//                        FriendGoalWithAchievement(
//                            goalId = goal.goalId,
//                            goalName = goal.goalName,
//                            goalType = goal.goalType,
//                            goalAmount = goal.goalAmount,
//                            verificationType = goal.verificationType,
//                            goalTime = goal.goalTime,
//                            frequency = goal.frequency,
//                            oneDose = goal.oneDose,
//                            totalAchievement = response.result.totalAchievement
//                        )
//                    )
//                }
//            } catch (e: Exception) {
//                Log.e("FriendGoalRepo", "달성률 API 예외: ${e.message}")
//            }
//        }
//        return resultList
//    }

    suspend fun getTodayAchievement() : ApiResult<DailyAchievementResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getDailyAchievement(LocalDate.now().toString())
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }
}
