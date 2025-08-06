package com.example.planup.main.friend.data

data class FriendRequestResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendInfo>
)