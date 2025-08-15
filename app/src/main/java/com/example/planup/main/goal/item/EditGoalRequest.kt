package com.example.planup.main.goal.item

data class EditGoalRequest(
    val goalName: String,
    val goalAmount: String,
    val goalCategory: String,
    val goalType: String,
    val oneDose: Int,
    val frequency: Int,
    val period: String,
    val endDate: String,
    val verificationType: String,
    val limitFriendCount: Int,
    val goalTime: Int
)

data class EditGoalResponse(
    val goalName: String,
    val goalAmount: String,
    val goalCategory: String,
    val goalType: String,
    val oneDose: Int,
    val frequency: Int,
    val period: String,
    val endDate: String,
    val verificationType: String,
    val limitFriendCount: Int,
    val goalTime: Int
)

data class EditGoalApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: EditGoalResponse
)