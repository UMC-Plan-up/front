package com.planup.planup.main.goal.item

data class GoalCreateLevelResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GoalCreateLevelResult
)

data class GoalCreateLevelResult(
    val userLevel: String,
)