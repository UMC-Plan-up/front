package com.example.planup.main.home.ui

import com.example.planup.database.TokenSaver
import com.example.planup.database.checkToken
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.goal.item.TotalAchievementResult
import com.example.planup.network.ApiResult
import com.example.planup.network.FriendApi
import com.example.planup.network.GoalApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val goalApi: GoalApi,
    private val friendApi: FriendApi,
    private val tokenSaver: TokenSaver
){
    suspend fun getMyGoalList() : ApiResult<List<MyGoalListItem>> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getMyGoalList(token)
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

    suspend fun loadTotalAchievement(goalId: Int): ApiResult<TotalAchievementResult> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getTotalAchievement(token, goalId)
                    },
                    onResponse = { response ->
                        if (response.isSuccess) {
                            val result = response.result
                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }
                    })
            }
        }

    suspend fun getFriendSummary() =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        friendApi.getFriendSummary()
                    },
                    onResponse = { response ->
                        if(response.isSuccess) {
                            val result = response.result
                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }
                    })
            }
        }

    suspend fun getFriendGoalList(friendId: Int) =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getFriendGoalList(token, friendId)
                    },
                    onResponse = { response ->
                        if(response.isSuccess) {
                            val result = response.result
                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }
                    })
            }
        }

    suspend fun getFriendGoalAchievement(friendId: Int, goalId: Int) =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getFriendGoalAchievement(token, friendId, goalId)
                    },
                    onResponse = { response ->
                        if(response.isSuccess) {
                            val result = response.result
                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }
                    })
            }
        }

    suspend fun getDailyGoal(date: LocalDate) =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getDailyGoal(token, date.toString())
                    },
                    onResponse = { response ->
                        if (response.isSuccess) {
                            val result = response.result
                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }
                    })
            }
        }
}
