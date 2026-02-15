package com.planup.planup.main.home.item

data class FriendChallengeItem (
    val friendId: Int,
    val name: String,
    val description: String,
    val profileResId: Int,
    val pieValues: List<Float>
)