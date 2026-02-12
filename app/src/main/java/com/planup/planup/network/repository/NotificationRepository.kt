package com.planup.planup.network.repository

import com.planup.planup.network.ApiResult

interface NotificationRepository {
    suspend fun getFcmToken(): String?
    suspend fun updateFcmToken(): ApiResult<Boolean>
    suspend fun updateFcmToken(token: String): ApiResult<Boolean>
}