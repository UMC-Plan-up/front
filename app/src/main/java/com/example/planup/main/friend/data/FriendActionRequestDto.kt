package com.example.planup.main.friend.data

data class FriendActionRequestDto (
    val friendId: Int
)

data class UnblockFriendRequestDto(
    val userId: Int,
    val friendNickname: String
)