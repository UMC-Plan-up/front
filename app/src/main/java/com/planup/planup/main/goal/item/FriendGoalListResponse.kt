package com.planup.planup.main.goal.item

data class FriendGoalListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendGoalListResult>
)

data class FriendGoalListResult(
    val goalId: Int,
    val goalName: String,
    val goalType: String,
    val goalAmount: String,
    val verificationType: String,
    val goalTime: Int,
    val frequency: Int,
    val oneDose: Int
)
