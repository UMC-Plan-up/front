package com.example.planup.network

import com.example.planup.network.data.SignupResult
import com.example.planup.network.data.UserResponse
import com.example.planup.network.dto.notification.NotificationPatchResponse
import com.example.planup.network.dto.notification.NotificationResponse
import com.example.planup.network.dto.notification.NotificationTypeResponse
import com.example.planup.signup.data.SignupRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    @GET("/notification/unread/{receiverId}")
    suspend fun loadNotification(
        @Path("receiverId") receiverId: Int
    ): Response<NotificationResponse>

    @GET("/notification/unread/{receiverId}/{type}")
    suspend fun loadNotificationType(
        @Path("receiverId") receiverId: Int,
        @Path("type") type: String
    ): Response<NotificationTypeResponse>

    @PATCH("/notification/{notificationId}")
    suspend fun patchNotification(
        @Query("userId") userId: Int,
        @Query("notificationId") notificationId: Int
    ): Response<NotificationPatchResponse>
}