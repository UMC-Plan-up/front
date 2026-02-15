package com.example.planup.main.home.ui.repository

import com.example.planup.network.GoalApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.planup.network.ApiResult
import com.example.planup.network.VerificationApi
import javax.inject.Singleton

@Singleton
class FriendGoalDetailRepository @Inject constructor(
    val goalApi: GoalApi,
    val verificationApi: VerificationApi
) {
    suspend fun loadComment(goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getComments(goalId)
                },
                onResponse = {
                    if (it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }

    suspend fun loadTodayFriendTime(goalId: Int, friendId: Int) {
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    verificationApi.getTodayFriendTimer(friendId, goalId)
                },
                onResponse = {
                    if (it.isSuccess) ApiResult.Success(it.result)
                    else ApiResult.Fail(it.message)
                }
            )

        }
    }
}