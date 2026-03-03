package com.planup.planup.network.repository

import com.planup.planup.main.home.ui.viewmodel.NotificationItem
import com.planup.planup.network.ApiResult
import com.planup.planup.network.dto.notification.NotificationResult

interface NotificationRepository {
    suspend fun getFcmToken(): String?
    suspend fun updateFcmToken(): ApiResult<Boolean>
    suspend fun updateFcmToken(token: String): ApiResult<Boolean>
    suspend fun removeFcmToken(): ApiResult<Boolean>
    suspend fun removeFcmToken(token: String): ApiResult<Boolean>
    suspend fun loadNotificationType(
        receiverId: Int,
        type: String
    ): ApiResult<List<NotificationItem>>
}