package com.example.planup.main.goal.item

data class DailyGoalResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DailyGoalResult?
)

data class DailyGoalResult(
    val date: String,
    val verifiedGoals: List<VerifiedGoal>,
    val totalCount: Int
)

data class VerifiedGoal(
    val goalName: String,
    val period: String,
    val frequency: Int
)