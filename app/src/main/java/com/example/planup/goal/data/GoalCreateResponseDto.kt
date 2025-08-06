package com.example.planup.goal.data

data class GoalCreateResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GoalResult
)

data class GoalResult(
    val goalId: Int,
    val goalName: String,
    val goalAmount: String,
    val goalCategory: String,
    val goalType: String,
    val oneDose: String,
    val frequency: Int,
    val period: String,
    val endDate: String,
    val verificationType: String,
    val limitFriendCount: Int
)
