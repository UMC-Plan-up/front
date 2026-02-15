package com.planup.planup.main.friend.data

import com.google.gson.annotations.SerializedName

data class ApiResponseListFriendGoalListDto(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message")  val message: String,
    @SerializedName("result")  val result: List<FriendGoalListDto>
)

data class FriendGoalListDto(
    @SerializedName("goalId")  val goalId: Int,
    @SerializedName("goalName")  val goalName: String,
    @SerializedName("goalType")  val goalType: GoalType,
    @SerializedName("goalAmount")  val goalAmount: String,
    @SerializedName("goalPeriod") val goalPeriod: String,
    @SerializedName("verificationType") val verificationType: VerificationType,
    @SerializedName("goalTime") val goalTime: Int,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("oneDose") val oneDose: Int

)

enum class GoalType{
    @SerializedName("FRIEND") FRIEND,
    @SerializedName("COMMUNITY") COMMUNITY,
    @SerializedName("CHALLENGE_PHOTO") CHALLENGE_PHOTO,
    @SerializedName("CHALLENGE_TIME") CHALLENGE_TIME,
}

enum class VerificationType{
    @SerializedName("PHOTO") PHOTO,
    @SerializedName("TIMER") TIMER
}