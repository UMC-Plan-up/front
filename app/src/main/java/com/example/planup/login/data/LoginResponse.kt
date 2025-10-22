package com.example.planup.login.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Result
) {
    data class Result(
        @SerializedName(value = "accessToken") val accessToken: String,
        @SerializedName(value = "nickname") val nickname: String,
        @SerializedName(value = "profileImgUrl") val profileImgUrl: String?,
        @SerializedName(value = "message") val message: String
    )

}