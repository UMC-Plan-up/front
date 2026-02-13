package com.planup.planup.main.home.data

data class NewPenaltyDto(
    val userId: Int,
    val penalty: String,
    val friendIdList: List<Long>
)
