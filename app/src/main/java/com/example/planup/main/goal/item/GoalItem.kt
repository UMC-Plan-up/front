package com.example.planup.main.goal.item

data class GoalItem(
    val title: String,
    val description: String,
    val percent: Int,
    var isActive: Boolean = true,
    var isEditMode: Boolean = false
)