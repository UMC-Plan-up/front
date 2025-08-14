package com.example.planup.network.dto.friend

import com.google.gson.annotations.SerializedName

data class FriendUnblockDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("friendNickname") val friendNickname: String
)
