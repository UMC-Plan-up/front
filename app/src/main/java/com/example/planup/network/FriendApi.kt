package com.example.planup.network

import com.example.planup.network.data.FriendBlockListResponse
import com.example.planup.network.dto.friend.BaseResponse
import com.example.planup.network.dto.friend.FriendReportRequestDto
import com.example.planup.network.dto.friend.FriendRequestsResponse
import com.example.planup.network.dto.friend.FriendResponseDto
import com.example.planup.network.dto.friend.UnblockFriendRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendApi {

    // 나에게 친구 신청한 친구 목록
    @GET("/friends/requests")
    suspend fun getFriendRequests(
        @Header("Authorization") token: String
    ): Response<FriendRequestsResponse>

    // 친구 화면 조회
    @GET("/friends/list")
    suspend fun getFriendSummary(
        @Header("Authorization") token: String
    ): Response<FriendResponseDto>

    // 차단된 친구 목록 조회
    @GET("/friends/blocked")
    suspend fun getBlockedFriendRequest(): Response<FriendBlockListResponse>

    // 친구 신청 수락
    @POST("/friends/accept")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String,
        @Query("friendId") goalId: Int
    ): Response<BaseResponse>

    // 친구 신청 거절
    @POST("/friends/reject")
    suspend fun rejectFriendRequest(
        @Header("Authorization") token: String,
        @Query("friendId") goalId: Int
    ): Response<BaseResponse>

    // 친구 차단
    @POST("/friends/block")
    suspend fun blockFriend(
        @Header("Authorization") token: String,
        @Query("friendId") request: Int
    ): Response<BaseResponse>

    //친구 삭제
    @POST("/friends/delete")
    suspend fun deleteFriend(
        @Header("Authorization") token: String,
        @Query("friendId") request: Int
    ): Response<BaseResponse>

    // 친구 차단 해제
    @POST("/friends/unblock")
    suspend fun unblockFriend(
        @Body request: UnblockFriendRequestDto
    ): Response<BaseResponse>

    // 친구 신고
    @POST("/friends/report")
    suspend fun reportFriend(
        @Body request: FriendReportRequestDto
    ): Response<BaseResponse>
}