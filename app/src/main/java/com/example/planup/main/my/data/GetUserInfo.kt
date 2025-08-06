package com.example.planup.main.my.data

import com.google.gson.annotations.SerializedName

data class GetUserInfo(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: User?
)
data class User(
    @SerializedName(value = "id") var id: Int,
    @SerializedName(value = "email") var email: String,
    @SerializedName(value = "nickname") var nickname: String,
    @SerializedName(value = "profileImg") var profileImage: String
)
