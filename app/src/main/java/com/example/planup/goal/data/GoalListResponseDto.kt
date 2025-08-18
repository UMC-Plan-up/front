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
    val frequency: Int,
    val oneDose: Int,
    val creatorNickname: String,
    val creatorProfileImg: String?,
    val participantCount: Int
)