package com.example.planup.main.user.data

import com.example.planup.network.data.UserInfo
import com.google.gson.annotations.SerializedName

//유저 정보 조회
data class UserInfoResponse(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: UserInfo
)