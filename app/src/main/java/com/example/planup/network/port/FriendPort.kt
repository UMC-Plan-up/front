package com.example.planup.network.port

import com.example.planup.network.data.BlockedFriends
import com.example.planup.network.data.FriendResponse
import com.example.planup.network.dto.friend.FriendReportDto
import com.example.planup.network.dto.friend.FriendUnblockDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FriendPort {
    //차단된 친구 목록 조회
    @GET("friends/blocked")
    fun blockedFriends(): Call<FriendResponse<List<BlockedFriends>>>

    //친구 차단 해제
    @POST("friends/unblock")
    fun unblockFriend(@Body friend: FriendUnblockDto): Call<FriendResponse<Boolean>>
    //친구 신고
    @POST("friends/report")
    fun reportFriend(@Body friend: FriendReportDto): Call<FriendResponse<Boolean>>
}