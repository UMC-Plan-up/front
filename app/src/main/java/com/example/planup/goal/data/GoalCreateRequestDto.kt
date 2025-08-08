package com.example.planup.goal.data

data class GoalCreateRequest(
    val goalName: String,
    val goalAmount: String,
    val goalCategory: String,
    val goalType: String,
    val oneDose: String,
    val frequency: Int,
    val period: String,
    val endDate: String,  
    val verificationType: String,
    val limitFriendCount: Int,
    val goalTime: Int
)
