package com.example.planup.main.goal.adapter

import com.example.planup.main.goal.data.MyGoalListDto

@Deprecated(
    message = "GoalRepository로 이관",
    replaceWith = ReplaceWith("GoalRepository")
)
interface MyGoalListDtoAdapter {
    fun successMyGoals(goals: List<MyGoalListDto>)
    fun failMyGoals(message: String)
}