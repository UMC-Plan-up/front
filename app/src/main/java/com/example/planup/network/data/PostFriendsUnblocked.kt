package com.example.planup.network.data

import com.google.gson.annotations.SerializedName

data class PostFriendsUnblocked(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Boolean
)
