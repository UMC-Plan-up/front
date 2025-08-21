package com.example.planup.network.data

import com.google.gson.annotations.SerializedName

//cahllenge controller 기본 응답 양식
data class ChallengeResponse<T>(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: T
)
//result가 Null인 response 양식
data class ChallengeResponseNoResult(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String
)

// 챌린지 결과 Response 양식
data class ChallengeResultResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ChallengeResult
)

// 챌린지 결과
data class ChallengeResult(
    @SerializedName("id") val id: Int,
    @SerializedName("myName") val myName: String,
    @SerializedName("myProfile") val myProfile: String,
    @SerializedName("friendName") val friendName: String,
    @SerializedName("friendProfile") val friendProfile: String,
    @SerializedName("penalty") val penalty: String,
    @SerializedName("myPercent") val myPercent: Int,
    @SerializedName("friendPercent") val friendPercent: Int
)

//챌린지 정보 조회
data class ChallengeInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("goalName") val goalName: String,
    @SerializedName("oneDose") val oneDose: String,
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

