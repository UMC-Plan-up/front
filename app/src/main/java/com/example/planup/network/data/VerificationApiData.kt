package com.example.planup.network.data

data class TodayTotalTimeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TodayTotalTimeResult
)
data class TodayTotalTimeResult(
    val formattedTime: String
)

data class TimerStartResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TimerStartResult
)
data class TimerStartResult(
    val timerId: Int,
    val userGoalId: Int,
    val goalTimeMinutes: Int,
    val startTime: String
)

data class TimerStopResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TimerStopResult
)

data class TimerStopResult(
    val timerId: Int,
    val userGoalId: Int,
    //val totalSpentTime: ~
    val goalTimeMinutes: Int,
    val endTime: String,
    val startTIme: String,
    val currentVerificationCount: Int,
    val goalAchieved: Boolean
)

data class UploadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Any?
)