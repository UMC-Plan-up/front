package com.example.planup.main.friend.network

import com.example.planup.main.friend.data.FriendListResponse
import retrofit2.Response
import retrofit2.http.GET

interface FriendApi {
    @GET("/your/api/path")  // 실제 API 경로로 변경
    suspend fun getFriendList(): Response<FriendListResponse>
}