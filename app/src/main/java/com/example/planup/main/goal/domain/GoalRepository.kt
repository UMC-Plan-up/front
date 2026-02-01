package com.example.planup.main.goal.domain

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalResult
import com.example.planup.main.goal.item.EditGoalApiResponse
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.network.ApiResult
import retrofit2.Response
import retrofit2.http.Body

interface GoalRepository {
    suspend fun updateGoal(goalId: Int, request: EditGoalRequest): Boolean
    suspend fun fetchMyGoals(): ApiResult<List<MyGoalListItem>>
    suspend fun deleteGoal(goalId: Int) : ApiResult<String>
    suspend fun setGoalActive(goalId: Int) : ApiResult<String>
    suspend fun createGoal(
        goalCreateRequest: GoalCreateRequest
    ): ApiResult<GoalResult>
    suspend fun getGoalDetail(goalId: Int): ApiResult<EditGoalResponse>
}