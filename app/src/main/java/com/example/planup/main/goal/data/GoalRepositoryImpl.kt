package com.example.planup.main.goal.data

import android.util.Log
import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalJoinResult
import com.example.planup.goal.data.GoalResult
import com.example.planup.main.goal.domain.GoalRepository
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.network.ApiResult
import com.example.planup.network.GoalApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalApi: GoalApi
) : GoalRepository {

    override suspend fun updateGoal(goalId: Int, request: EditGoalRequest): Boolean {
        try {
            val response = goalApi.editGoal(goalId = goalId, editGoalRequest = request)

            if (response.isSuccess) {
                Log.d("EditGoalFragment", "성공 메시지: ${response.message}")
                return true
            } else {
                val errorMessage = response.message
                Log.d("EditGoalFragment", "에러 메시지: $errorMessage")
                return false
            }
        } catch (e: Exception) {
            if (e is HttpException) {
                Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
            } else {
                Log.e("API", "Other error: ${e.message}", e)
            }
        }
        return false
    }

    override suspend fun fetchMyGoals() =
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

    override suspend fun deleteGoal(goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.deleteGoal(goalId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        Log.w(
                            "GoalFragment",
                            "deleteGoal fail body=${response} code=${response.code}"
                        )
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun setGoalActive(goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.setGoalActive(goalId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        Log.w(
                            "GoalFragment",
                            "setGoalActive fail body=${response} code=${response.code}"
                        )
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun createGoal(goalCreateRequest: GoalCreateRequest): ApiResult<GoalResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.createGoal(goalCreateRequest)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        Log.w(
                            "GoalFragment",
                            "setGoalActive fail body=${response} code=${response.code}"
                        )
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun getGoalDetail(goalId: Int): ApiResult<EditGoalResponse> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getEditGoal(goalId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        Log.w(
                            "GoalFragment",
                            "getGoalDetail fail body=${response} code=${response.code}"
                        )
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun joinGoal(goalId: Int): ApiResult<GoalJoinResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.joinGoal(goalId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        Log.w(
                            "GoalFragment",
                            "joinGoal fail body=${response} code=${response.code}"
                        )
                        ApiResult.Fail(response.message)
                    }

                }
            )
        }
}