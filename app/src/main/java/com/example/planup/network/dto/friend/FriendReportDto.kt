package com.example.planup.network.dto.friend

import com.google.gson.annotations.SerializedName

data class FriendReportDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("friendId") val friendId: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("block") val block: Boolean
)