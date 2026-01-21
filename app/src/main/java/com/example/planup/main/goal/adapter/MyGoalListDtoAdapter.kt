package com.example.planup.main.goal.adapter

import com.example.planup.main.goal.item.MyGoalListItem

interface MyGoalListDtoAdapter {
    fun successMyGoals(goals: List<MyGoalListItem>)
    fun failMyGoals(message: String)
}