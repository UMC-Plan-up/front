package com.example.planup.network.data.user

import com.google.gson.annotations.SerializedName

data class PostPasswordChange(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Boolean
)
