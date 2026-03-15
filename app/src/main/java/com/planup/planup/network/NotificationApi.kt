package com.planup.planup.network

import com.planup.planup.network.dto.notification.NotificationPatchResponse
import com.planup.planup.network.dto.notification.NotificationResponse
import com.planup.planup.network.dto.notification.NotificationTypeResponse
import com.planup.planup.network.dto.notification.RemoveDeviceTokenResponse
import com.planup.planup.network.dto.notification.UpdateDeviceTokenRequest
import com.planup.planup.network.dto.notification.UpdateDeviceTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    companion object{
        const val API_BASE_URL = "notifications/"
        const val CHALLENGE_UNREAD = "${API_BASE_URL}unread/"
    }
    @GET("${CHALLENGE_UNREAD}{receiverId}")
    suspend fun loadNotification(
        @Path("receiverId") receiverId: Int
    ): Response<NotificationResponse>

    @GET("${CHALLENGE_UNREAD}{receiverId}/{type}")
    suspend fun loadNotificationType(
        @Path("receiverId") receiverId: Int,
        @Path("type") type: String
    ): Response<NotificationTypeResponse>

    @PATCH("${API_BASE_URL}{notificationId}")
    suspend fun patchNotification(
        @Query("userId") userId: Int,
        @Query("notificationId") notificationId: Int
    ): Response<NotificationPatchResponse>

    @POST("deviceToken/post")
    suspend fun updateDeviceToken(
        @Body deviceToken: UpdateDeviceTokenRequest
    ): Response<UpdateDeviceTokenResponse>

    @PATCH("deviceToken/deactivate/token/{token}")
    suspend fun removeDeviceToken(
        @Query("token") token: String
    ): Response<RemoveDeviceTokenResponse>
}