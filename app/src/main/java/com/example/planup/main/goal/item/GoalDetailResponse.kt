package com.example.planup.main.goal.item

// GoalResponse.kt
data class GoalDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GoalResult
)

data class GoalResult(
    val goalId: Int,
    val goalName: String,
    val oneDose: Int, // 목표 1회량
    val public: Boolean,
    val commentList: List<Comment>
)

data class Comment(
    val goal: GoalDetail
)

data class GoalDetail(
    val createdAt: String, // 목표 생성일 (시작일)
    val endDate: String,   // 목표 종료일
    val period: String,
    val oneDose: Int,
    val userGoals: List<UserGoal>
)

data class UserGoal(
    val id: Int,
    val verificationCount: Int,
    val user: String, // 실제로는 userId를 참조
    val timerVerifications: List<TimerVerification>
)

data class TimerVerification(
    val createdAt: String // 날짜 비교를 위해 사용
)
