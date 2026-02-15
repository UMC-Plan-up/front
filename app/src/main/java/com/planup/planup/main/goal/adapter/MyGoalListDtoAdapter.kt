package com.planup.planup.main.goal.adapter

import com.planup.planup.main.goal.item.MyGoalListItem

interface MyGoalListDtoAdapter {
    fun successMyGoals(goals: List<MyGoalListItem>)
    fun failMyGoals(message: String)
}