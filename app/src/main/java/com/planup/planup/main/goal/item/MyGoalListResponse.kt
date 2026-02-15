package com.planup.planup.main.goal.item

data class MyGoalListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<MyGoalListItem>
)

data class MyGoalListItem(
    val goalId: Int,
    val goalName: String,
    val goalType: String,
    val frequency: Int,
    val oneDose: Int
)
