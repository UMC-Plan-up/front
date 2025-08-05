package com.example.planup.main.goal.item

data class EditGoalRequest(
    val title: String,
    val oneDose: String,
    val authType: String,
    val time: String?, // "HH:mm:ss" 형식
    val duration: String,
    val frequency: Int,
    val endDate: String // "yyyy-MM-dd"
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