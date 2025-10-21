package com.example.planup.main.user.data

import com.google.gson.annotations.SerializedName

//유저 정보 조회
data class UserInfoResponse(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Result
) {
    data class Result(
        @SerializedName(value = "id") var id: Int,
        @SerializedName(value = "email") var email: String,
        @SerializedName(value = "nickname") var nickname: String,
        @SerializedName(value = "profileImg") var profileImage: String
    )
}