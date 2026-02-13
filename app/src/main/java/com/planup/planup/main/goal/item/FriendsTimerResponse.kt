package com.planup.planup.main.goal.item

data class FriendsTimerResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendsTimerResult>
)

data class FriendsTimerResult(
    val userId: Int,
    val nickname: String,
    val profileImg: String,
    val todayTime: String,
    val verificationType: String
)
