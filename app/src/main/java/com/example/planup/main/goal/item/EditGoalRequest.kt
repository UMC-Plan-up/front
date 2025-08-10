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
    val title: String,
    val oneDose: String,
    val authType: String,
    val time: String?,
    val duration: String,
    val frequency: Int,
    val endDate: String
)

data class EditGoalApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)