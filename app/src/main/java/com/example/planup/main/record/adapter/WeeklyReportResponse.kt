package com.example.planup.main.record.adapter

data class WeeklyReportResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: WeeklyReportResult
)

data class WeeklyReportResult(
    val badgeDTOList: List<BadgeDTO>,
    val notificationDTOList: List<NotificationDTO>,
    val cheering: String
)

data class BadgeDTO(
    val badgeType: String,
    val badgeName: String
)

data class NotificationDTO(
    val id: Int,
    val notificationText: String,
    val url: String,
    val createdAt: String
)