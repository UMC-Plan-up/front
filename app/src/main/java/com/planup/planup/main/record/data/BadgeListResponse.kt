package com.planup.planup.main.record.data

data class BadgeListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<BadgeListResult>
)

data class BadgeListResult(
    val id: Int,
    val badgeName: String,
    val badgeType: String
)