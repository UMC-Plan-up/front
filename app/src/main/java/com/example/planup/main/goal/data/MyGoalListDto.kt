package com.example.planup.main.goal.data

import com.google.gson.annotations.SerializedName

data class MyGoalListDto(
    @SerializedName("goalId") val goalId: Int,
    @SerializedName("goalName") val goalName: String?,
    @SerializedName("goalType") val goalType: GoalType,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("oneDose") val oneDose: Int
)

enum class GoalType {
    @SerializedName("FRIEND") FRIEND,
    @SerializedName("COMMUNITY") COMMUNITY,
    @SerializedName("CHALLENGE_PHOTO") CHALLENGE_PHOTO,
    @SerializedName("CHALLENGE_TIME") CHALLENGE_TIME
}

data class ApiResponseListMyGoalListDto(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<MyGoalListDto>
)