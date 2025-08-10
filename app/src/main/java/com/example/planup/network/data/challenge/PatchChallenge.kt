package com.example.planup.network.data.challenge

import com.google.gson.annotations.SerializedName

data class PatchChallenge(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Boolean
)
