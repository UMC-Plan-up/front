package com.example.planup.network

import com.example.planup.main.friend.data.BaseResponse
import com.example.planup.main.friend.data.FriendActionRequestDto
import com.example.planup.main.friend.data.FriendRequestResponseDto
import com.example.planup.main.friend.data.FriendResponseDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FriendApi {

    @GET("/friends/requests")
    suspend fun getFriendRequests(
        @Header("Authorization") token: String
    ): Response<FriendRequestResponseDto>

    @GET("/friends/list")
    suspend fun getFriendSummary(
        @Header("Authorization") token: String
    ): Response<FriendResponseDto>

    @POST("/friends/accept")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String,
        @Body request: FriendActionRequestDto
    ): Response<BaseResponse>

    @POST("/friends/reject")
    suspend fun rejectFriendRequest(
        @Header("Authorization") token: String,
        @Body request: FriendActionRequestDto
    ): Response<BaseResponse>
}