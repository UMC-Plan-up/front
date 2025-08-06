package com.example.planup.main.my.data

import com.google.gson.annotations.SerializedName

data class Logout(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: String
)
