package com.example.planup.network.data

import com.google.gson.annotations.SerializedName


data class FriendResponse<T>(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: T
)
data class BlockedFriends(
    @SerializedName("friendId") val friendId: Int,
    @SerializedName("friendNickname") val friendNickname: String
)
