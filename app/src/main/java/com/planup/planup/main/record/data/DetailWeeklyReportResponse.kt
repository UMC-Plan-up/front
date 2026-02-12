package com.planup.planup.main.record.data

// 주간 리포트 전체 응답
data class DetailWeeklyReportResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DetailWeeklyReportResult
)

data class DetailWeeklyReportResult(
    val id: Int,
    val year: Int,
    val month: Int,
    val weekNumber: Int,
    val startDate: String,
    val endDate: String,
    val nextGoalMessage: String,
    val goalReports: List<GoalReport>,
    val totalDailyAchievement: Int,
    val dailyRecordList: List<DailyRecord>,
    val badgeList: List<Badge>
)

// ⚠️ 서버가 목표 성격을 넘겨줄 수 있도록 확장 필드 추가:
//   - goalType: "FRIEND" / "COMMUNITY" / (그 외) 등 텍스트 기반
//   - isCommunity: Boolean 기반
// 둘 중 하나만 내려와도 동작하도록 모두 옵셔널 처리
data class GoalReport(
    val id: Int,
    val goalTitle: String?,
    val goalCriteria: String?,
    val achievementRate: Int?,
    val goalType: String? = null,     // ← 서버에서 제공 시 사용
    val isCommunity: Boolean? = null  // ← 서버에서 제공 시 사용
)

data class DailyRecord(
    val id: Int?,
    val date: String?,
    val recordedTime: Int?,
    val photoVerified: String?, // "data:image/...;base64,..." 형식일 수 있어 Base64 디코딩 필요
    val simpleMessage: String?
)

data class Badge(
    val id: Int?,
    val badgeName: String?,
    val badgeType: String?
)