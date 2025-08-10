package com.example.planup.main.record.adapter

data class ApiResponseGoalReportResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GoalReportResponse
)

data class GoalReportResponse(
    val id: Int,
    val goalId: Int,
    val goalTitle: String,
    val goalCriteria: String,
    val achievementRate: Int,
    val reportType: String,
    val threeWeekAchievementRate: List<ThreeWeekAchievementRateResponse>,
    val dailyAchievementRate: List<DailyAchievementRateResponse>,
    val reportUsers: List<ReportUsersResponse>
)

data class ThreeWeekAchievementRateResponse(
    val thisWeek: Int,
    val oneWeekBefore: Int,
    val twoWeekBefore: Int
)

data class DailyAchievementRateResponse(
    val mon: Int,
    val tue: Int,
    val wed: Int,
    val thu: Int,
    val fri: Int,
    val sat: Int,
    val sun: Int,
    val total: Int
)

data class ReportUsersResponse(
    val id: Int,
    val userName: String,
    val rate: Int
)