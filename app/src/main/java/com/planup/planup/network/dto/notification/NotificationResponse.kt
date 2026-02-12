package com.planup.planup.network.dto.notification

data class NotificationResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<NotificationResult>
)
data class NotificationResult(
    val id: Int,
    val notificationText: String,
    val url: String,
    val createdAt: String
)

data class NotificationTypeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<NotificationResult>
)

data class NotificationPatchResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean //정보 없음
)