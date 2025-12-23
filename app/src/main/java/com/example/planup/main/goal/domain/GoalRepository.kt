package com.example.planup.main.goal.domain

import com.example.planup.main.goal.item.EditGoalRequest

interface GoalRepository {
    suspend fun updateGoal(token:String, goalId: Int, request: EditGoalRequest): Boolean
}