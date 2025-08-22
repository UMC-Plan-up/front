package com.example.planup.main.goal.item

data class DailyAchievementResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DailyAchievementResult
)
data class DailyAchievementResult(
    val date: String,
    val achievementRate: Int
)

data class TotalAchievementResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TotalAchievementResult
)

data class TotalAchievementResult(
    val goalId: Int,
    val totalAchievementRate: Int
)

data class FriendGoalAchievementResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FriendGoalAchievementResult
)

data class FriendGoalAchievementResult(
    val goalId: Int,
    val totalAchievement: Int
)