package com.example.planup.goal.data

data class GoalJoinResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GoalJoinResult
)

data class GoalJoinResult(
    val goalId: Int,
    val goalTitle: String,
    val userId: Int,
    val status: String,
    val goalType: String
)