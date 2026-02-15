package com.planup.planup.main.record.data


import com.google.gson.annotations.SerializedName

data class ChallengeWeeklySummary(
    @SerializedName("id") val id: Int,
    @SerializedName("myName") val myName: String,
    @SerializedName("myProfile") val myProfile: String?,       // URL 또는 Base64 등이라면 String으로 수신
    @SerializedName("friendName") val friendName: String,
    @SerializedName("friendProfile") val friendProfile: String?,// 동일
    @SerializedName("penalty") val penalty: String,
    @SerializedName("myPercent") val myPercent: Int,            // 0..100
    @SerializedName("friendPercent") val friendPercent: Int     // 0..100
)