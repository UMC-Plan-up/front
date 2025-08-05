package com.example.planup.main.goal.item

data class GoalItem(
    val goalId: Long,
    val title: String,
    val description: String,
    val percent: Int,
    val authType: String = "camera",
    var isActive: Boolean = true,
    var isEditMode: Boolean = false
)