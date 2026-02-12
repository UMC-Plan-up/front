package com.planup.planup.main.goal.data

import com.google.gson.annotations.SerializedName

data class MyGoalListDto(
    @SerializedName("goalId") val goalId: Int,
    @SerializedName("goalName") val goalName: String?,
    @SerializedName("goalType") val goalType: GoalType,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("oneDose") val oneDose: Int,
    @SerializedName("active") val isActive: Boolean
)

enum class GoalType {
    @SerializedName("FRIEND") FRIEND,
    @SerializedName("COMMUNITY") COMMUNITY,
    @SerializedName("CHALLENGE_PHOTO") CHALLENGE_PHOTO,
    @SerializedName("CHALLENGE_TIME") CHALLENGE_TIME
}