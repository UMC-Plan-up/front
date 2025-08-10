package com.example.planup.network.data.user

import com.google.gson.annotations.SerializedName

data class PatchWithdraw(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResponseWithDraw
)
data class ResponseWithDraw(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("withdrawableDate") val data: String
)
