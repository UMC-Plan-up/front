package com.example.planup.main.record.data

import com.google.gson.annotations.SerializedName

data class ApiResponseWeeklyReportResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: WeeklyReportResponseDto
)

data class WeeklyReportResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("year") val year: Int,
    @SerializedName("month") val month: Int,
    @SerializedName("weekNumber") val weekNumber: Int,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("nextGoalMessage") val nextGoalMessage: String,
    @SerializedName("goalReports") val goalReports: SimpleGoalReport,
    @SerializedName("totalDailyAchievement") val totalDailyAchievement: DailyAchievementRate,
    @SerializedName("dailyRecordList") val dailyRecordList:DailyRecordDTO,
    @SerializedName("badgeList") val badgeList: SimpleBadgeDTO
)

data class SimpleGoalReport(
    @SerializedName("id") val id: Int,
    @SerializedName("goalTitle") val goalTitle: String,
    @SerializedName("goalCriteria") val goalCriteria: String,
    @SerializedName("achievementRate") val achievementRate: Int
)

data class DailyAchievementRate(
    @SerializedName("mon") val mon: Int,
    @SerializedName("tue") val tue: Int,
    @SerializedName("wed") val wed: Int,
    @SerializedName("thu") val thu: Int,
    @SerializedName("fri") val fri: Int,
    @SerializedName("sat") val sat: Int,
    @SerializedName("sun") val sun: Int,
    @SerializedName("total") val total: Int
)

data class DailyRecordDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("recordedTime") val recordedTime: String,
    @SerializedName("photoVerified") val photoVerified: String,
    @SerializedName("simpleMessage") val simpleMessage: String
)

data class SimpleBadgeDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("badgeName") val badgeName: String,
    @SerializedName("badgeType") val badgeType: String
)

enum class BadgeTyp{
    @SerializedName("INFLUENTIAL_STARTER") INFLUENTIAL_STARTER,
    @SerializedName("WORD_OF_MOUTH_MASTER") WORD_OF_MOUTH_MASTER,
    @SerializedName("MAGNET_USER") MAGNET_USER,
    @SerializedName("FRIENDLY_MAX") FRIENDLY_MAX,
    @SerializedName("FIRST_COMMENT") FIRST_COMMENT,
    @SerializedName("FRIEND_REQUEST_KING") FRIEND_REQUEST_KING,
    @SerializedName("PROFILE_CLICKER") PROFILE_CLICKER,
    @SerializedName("FEEDBACK_CHAMPION") FEEDBACK_CHAMPION,
    @SerializedName("COMMENT_FAIRY") COMMENT_FAIRY,
    @SerializedName("CHEER_MASTER") CHEER_MASTER,
    @SerializedName("REACTION_EXPERT") REACTION_EXPERT,
    @SerializedName("START_OF_CHALLENGE") START_OF_CHALLENGE,
    @SerializedName("DILIGENT_TRACKER") DILIGENT_TRACKER,
    @SerializedName("ROUTINER") ROUTINER,
    @SerializedName("IMMERSION_DAY") IMMERSION_DAY,
    @SerializedName("GOAL_COLLECTOR") GOAL_COLLECTOR,
    @SerializedName("NOTIFICATION_STARTER") NOTIFICATION_STARTER,
    @SerializedName("ANALYST") ANALYST,
    @SerializedName("CONSISTENT_RECORDER") CONSISTENT_RECORDER
}