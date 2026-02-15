package com.planup.planup.main.goal.item

data class GoalItem(
    val goalId: Int,
    val title: String,
    val description: String,
    val percent: Int,
    val authType: String = "camera",
    var isActive: Boolean = true,
    val isEditMode: Boolean = false,
    val criteria: String,
    val progress: Int
)