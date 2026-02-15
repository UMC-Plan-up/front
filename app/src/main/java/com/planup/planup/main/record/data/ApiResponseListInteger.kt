package com.planup.planup.main.record.data

import com.google.gson.annotations.SerializedName

data class ApiResponseListInteger(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Int
)
