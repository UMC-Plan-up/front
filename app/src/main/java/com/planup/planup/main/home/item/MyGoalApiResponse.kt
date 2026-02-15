package com.planup.planup.main.home.item

data class MyGoalApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<MyGoalResult>
)

data class MyGoalResult(
    val goalId: Int,
    val goalName: String,
    val goalCategory: String,
    val goalType: String,
    val verificationType: String,
    val goalTime: Int,
    val spentTimeMinutes: Int,
    val frequency: Int,
    val oneDose: String,
    val creatorNickname: String,
    val creatorProfileImg: String,
    val myStatus: String,
    val participantCount: Int,
    val `public`: Boolean,
    val active: Boolean
)
