package com.example.planup.network.data.user

import com.google.gson.annotations.SerializedName

data class GetKakao(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Kakao?
)
data class Kakao(
    @SerializedName(value = "kakaoEmail") var kakaoEmail: String,
    @SerializedName(value = "linked") var linked: Boolean
)
