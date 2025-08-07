package com.example.planup.goal.data

data class GoalListResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<GoalItemDto>
)

data class GoalItemDto(
    val goalId: Int,
    val goalName: String,
    val goalCategory: String,
    val goalType: String,
    val verificationType: String,
    val goalTime: Int,
    val startTimeMinutes: Int,
    val frequency: Int,
    val oneDesc: String,
    val creatorNickname: String,
    val creatorProfileImg: String,
    val participantCount: Int
)
