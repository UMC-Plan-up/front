package com.example.planup.main.goal.adapter

import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.item.GoalItem
import kotlinx.coroutines.flow.StateFlow

interface MyGoalListDtoAdapter {
    fun successMyGoals(goals: List<MyGoalListDto>)
    fun failMyGoals(message: String)
}