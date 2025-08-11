package com.example.planup.main.goal.item

data class FriendGoalListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendGoalApiItem>
)

data class FriendGoalApiItem(
    val goalId: Int,
    val goalName: String,
    val goalType: String,
    val verificationType: String,
    val goalTime: Int,
    val frequency: Int,
    val oneDose: Int
)
