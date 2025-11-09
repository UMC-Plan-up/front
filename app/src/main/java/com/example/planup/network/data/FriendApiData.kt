package com.example.planup.network.data

import com.google.gson.annotations.SerializedName

//Friend Controller 기본 응답 양식
data class FriendResponse<T>(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: T
)

data class BlockFriendResponse(
    @SerializedName("friendId") val friendId: Int,
    @SerializedName("friendNickname") val friendNickname: String
)


typealias FriendBlockListResponse = FriendResponse<List<BlockFriendResponse>>

typealias FriendUnblockResponse = FriendResponse<Boolean>

typealias FriendBlockResponse = FriendResponse<Boolean>

typealias FriendReportResponse = FriendResponse<Boolean>