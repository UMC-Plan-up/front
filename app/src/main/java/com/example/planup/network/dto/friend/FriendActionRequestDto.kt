package com.example.planup.network.dto.friend

data class FriendActionRequestDto (
    val friendId: Int
)

data class UnblockFriendRequestDto(
    val userId: Int,
    val friendNickname: String
)