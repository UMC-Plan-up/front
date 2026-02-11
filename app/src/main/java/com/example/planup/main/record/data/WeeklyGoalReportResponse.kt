package com.example.planup.main.record.data

data class WeeklyGoalReportResponse (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: WeeklyGoalReportResult
)

data class WeeklyGoalReportResult(
    val id: Long,
    val goalId: Long,
    val goalTitle: String,
    val goalCriteria: String,
    val achievementRate: Int,
    val reportType: String,
    val threeWeekAchievementRate: ThreeWeekAchievementRateDto,
    val dailyAchievementRate: DailyAchievementRateDto,
    val reportUsers: List<ReportUserDto>,
    val comments: List<CommentDto>
)

data class ThreeWeekAchievementRateDto(
    val thisWeek: Int,
    val oneWeekBefore: Int,
    val twoWeekBefore: Int
)

data class DailyAchievementRateDto(
    val mon: Int,
    val tue: Int,
    val wed: Int,
    val thu: Int,
    val fri: Int,
    val sat: Int,
    val sun: Int,
    val total: Int
)

data class ReportUserDto(
    val id: Long,
    val userName: String,
    val rate: Int,
    val profileImg: String
)

data class CommentDto(
    val id: Long,
    val content: String,
    val writerId: Long,
    val writerNickname: String,
    val writerProfileImg: String,
    val parentCommentId: Long?,
    val parentCommentContent: String?,
    val parentCommentWriter: String?,
    val myComment: Boolean,
    val reply: Boolean
)

data class RankingListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<RankingListResult>
)

data class RankingListResult(
    val goalId: Long,
    val userId: Long,
    val nickName: String,
    val profileImg: String,
    val verificationCount: Int
)