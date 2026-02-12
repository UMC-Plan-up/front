package com.planup.planup.main.record.data

import com.google.gson.annotations.SerializedName

data class WeeklyReportResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: WeeklyReportResult
)

data class WeeklyReportResult(
    // 서버가 badgeDTOList 또는 bedgeDTOList 로 줄 수 있어 모두 대응
    @SerializedName(value = "badgeDTOList", alternate = ["bedgeDTOList"])
    val badgeDTOList: List<BadgeDTO> = emptyList(),
    val notificationDTOList: List<NotificationDTO> = emptyList(),
    val cheering: String? = null
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