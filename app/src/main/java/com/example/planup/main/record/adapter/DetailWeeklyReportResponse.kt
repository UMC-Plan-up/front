package com.example.planup.main.record.adapter

data class DetailWeeklyReportResponse(
    val isSuccess: Boolean,
    val code: String?,
    val message: String?,
    val result: DetailWeeklyReportResult?
)

data class DetailWeeklyReportResult(
    val id: Int?,
    val year: Int?,
    val month: Int?,
    val weekNumber: Int?,
    val startDate: String?,
    val endDate: String?,
    val nextGoalMessage: String?,
    val goalReports: List<GoalReport>?,
    val totalDailyAchievement: Int?,
    val dailyRecordList: List<DailyRecord>?,
    val badgeList: List<Badge>?
)

data class GoalReport(
    val id: Int,
    val goalTitle: String?,
    val goalCriteria: String?,
    val achievementRate: Int?
)

data class DailyRecord(
    val id: Int?,
    val date: String?,
    val recordedTime: Int?,
    val photoVerified: String?,
    val simpleMessage: String?
)

data class Badge(
    val id: Int?,
    val badgeName: String?,
    val badgeType: String?
)