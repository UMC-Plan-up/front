package com.planup.planup.main.home.ui

import com.planup.planup.database.TokenSaver
import com.planup.planup.main.goal.item.MyGoalListItem
import com.planup.planup.main.goal.item.TotalAchievementResult
import com.planup.planup.network.ApiResult
import com.planup.planup.network.FriendApi
import com.planup.planup.network.GoalApi
import com.planup.planup.network.UserApi
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val goalApi: GoalApi,
    private val friendApi: FriendApi,
    private val userApi: UserApi,
    private val tokenSaver: TokenSaver
){
    suspend fun getMyGoalList() : ApiResult<List<MyGoalListItem>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getMyGoalList()
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

    suspend fun loadTotalAchievement(goalId: Int): ApiResult<TotalAchievementResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getTotalAchievement(goalId)
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

    suspend fun getFriendSummary() =
        withContext(Dispatchers.IO) {
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
                }
            )

        }

    suspend fun getFriendGoalList(friendId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getFriendGoalList(friendId)
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

    suspend fun getFriendGoalAchievement(friendId: Int, goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getFriendGoalAchievement(friendId, goalId)
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

    suspend fun getDailyGoal(date: LocalDate) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getDailyGoal(date.toString())
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

    suspend fun getUserInfo() =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.getUserInfo()
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
