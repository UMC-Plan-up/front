package com.example.planup.network.data

import com.google.gson.annotations.SerializedName

//cahllenge controller 기본 응답 양식
data class ChallengeResponse<T>(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: T
)

//챌린지 정보 조회
data class ChallengeInfo(
    @SerializedName("id") val id: String,
    @SerializedName("code") val name: String,
    @SerializedName("goalName") val goalName: String,
    @SerializedName("goalAmount") val goalAmount: String,
    @SerializedName("goalType") val goalType: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("period") val period: String,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("targetTime") val targetTime: Int
)

//챌린지에서 친구 조회
data class ChallengeFriends(
    @SerializedName("id") val id: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("goalCnt") val goalCnt: Int
)

