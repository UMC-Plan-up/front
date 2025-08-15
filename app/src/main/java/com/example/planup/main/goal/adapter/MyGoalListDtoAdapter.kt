package com.example.planup.main.goal.adapter

import com.example.planup.main.goal.data.MyGoalListDto

interface MyGoalListDtoAdapter {
    fun successMyGoals(goals: List<MyGoalListDto>)
    fun failMyGoals(message: String)
}