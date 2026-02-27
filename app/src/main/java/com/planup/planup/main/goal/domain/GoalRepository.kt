package com.planup.planup.main.goal.domain

import com.planup.planup.goal.data.GoalCreateRequest
import com.planup.planup.goal.data.GoalJoinResult
import com.planup.planup.goal.data.GoalResult
import com.planup.planup.main.goal.item.EditGoalRequest
import com.planup.planup.main.goal.item.EditGoalResponse
import com.planup.planup.main.goal.item.MyGoalListItem
import com.planup.planup.network.ApiResult

interface GoalRepository {
    suspend fun updateGoal(goalId: Int, request: EditGoalRequest): Boolean
    suspend fun fetchMyGoals(): ApiResult<List<MyGoalListItem>>
    suspend fun deleteGoal(goalId: Int) : ApiResult<String>
    suspend fun setGoalActive(goalId: Int) : ApiResult<String>
    suspend fun createGoal(
        goalCreateRequest: GoalCreateRequest
    ): ApiResult<GoalResult>
    suspend fun getGoalDetail(goalId: Int): ApiResult<EditGoalResponse>
    suspend fun joinGoal(goalId: Int): ApiResult<GoalJoinResult>
    suspend fun getGoalLevel(): ApiResult<String>
}