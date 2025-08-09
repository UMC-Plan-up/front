package com.example.planup.network.data.challenge

import com.google.gson.annotations.SerializedName

data class GetChallengeFriends(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<ChallengeFriends>
)
data class ChallengeFriends(
    @SerializedName("id") val id: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("goalCnt") val goalCnt: Int
)