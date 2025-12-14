package com.example.planup.network

import com.example.planup.network.data.FriendAcceptResponse
import com.example.planup.network.data.FriendBlockListResponse
import com.example.planup.network.data.FriendBlockResponse
import com.example.planup.network.data.FriendDeclineResponse
import com.example.planup.network.data.FriendDeleteResponse
import com.example.planup.network.data.FriendReportResponse
import com.example.planup.network.data.FriendUnblockResponse
import com.example.planup.network.dto.friend.FriendReportRequestDto
import com.example.planup.network.dto.friend.FriendRequestsResponse
import com.example.planup.network.dto.friend.FriendResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendApi {

    // 나에게 친구 신청한 친구 목록
    @GET("/friends/requests")
    suspend fun getFriendRequests(): Response<FriendRequestsResponse>

    // 친구 화면 조회
    @GET("/friends/list")
    suspend fun getFriendSummary(): Response<FriendResponseDto>

    // 차단된 친구 목록 조회
    @GET("/friends/blocked")
    suspend fun getBlockedFriendRequest(): Response<FriendBlockListResponse>

    // 친구 신청 수락
    @POST("/friends/accept")
    suspend fun acceptFriend(
        @Query("friendId") request: Int
    ): Response<FriendAcceptResponse>

    // 친구 신청 거절
    @POST("/friends/reject")
    suspend fun rejectFriend(
        @Query("friendId") request: Int
    ): Response<FriendDeclineResponse>

    // 친구 차단
    @POST("/friends/block")
    suspend fun blockFriend(
        @Query("friendId") request: Int
    ): Response<FriendBlockResponse>

    //친구 삭제
    @POST("/friends/delete")
    suspend fun deleteFriend(
        @Query("friendId") request: Int
    ): Response<FriendDeleteResponse>

    // 친구 차단 해제
    @POST("/friends/unblock")
    suspend fun unblockFriend(
        @Query("friendId") request: Int
    ): Response<FriendUnblockResponse>

    // 친구 신고
    @POST("/friends/report")
    suspend fun reportFriend(
        @Body request: FriendReportRequestDto
    ): Response<FriendReportResponse>
}