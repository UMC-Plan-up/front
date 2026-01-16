package com.example.planup.main.goal.domain

import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.network.ApiResult

interface GoalRepository {
    suspend fun updateGoal(token:String, goalId: Int, request: EditGoalRequest): Boolean
    suspend fun fetchMyGoals(): ApiResult<List<MyGoalListItem>>
    suspend fun deleteGoal(goalId: Int) : ApiResult<String>
    suspend fun setGoalActive(goalId: Int) : ApiResult<String>
}