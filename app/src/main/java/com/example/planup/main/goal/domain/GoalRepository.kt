package com.example.planup.main.goal.domain

import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.goal.item.MyGoalListResponse
import com.example.planup.network.ApiResult

interface GoalRepository {
    suspend fun updateGoal(token:String, goalId: Int, request: EditGoalRequest): Boolean
    suspend fun fetchMyGoals(): ApiResult<List<MyGoalListItem>>
}