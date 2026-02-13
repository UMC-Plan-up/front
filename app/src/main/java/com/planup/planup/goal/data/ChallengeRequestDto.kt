package com.planup.planup.goal.data

import com.google.gson.annotations.SerializedName

data class ChallengeInfo(
    @SerializedName("goalName") val goalName: String,
    @SerializedName("goalAmount") val goalAmount: String,
    @SerializedName("goalType") val goalType: String,
    @SerializedName("oneDose") val oneDose: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("penalty") val penalty: String,
    @SerializedName("friendIdList") val friendId: Int,
    @SerializedName("photoChallenge") val photo: ChallengePhoto,
    @SerializedName("timeChallenge") val timer: ChallengeTimer
)
data class ChallengePhoto(
    @SerializedName("timePerPeriod") val period: Int,
    @SerializedName("frequency") val frequency: Int
)
data class ChallengeTimer(
    @SerializedName("targetTime") val time: Int
)