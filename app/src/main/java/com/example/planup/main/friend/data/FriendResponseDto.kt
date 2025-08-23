package com.example.planup.main.friend.data

data class FriendResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendResult>
)

data class FriendResult(
    val cnt: Int,
    val friendInfoSummaryList: List<FriendInfo>
)

data class FriendInfo(
    val id: Int,
    val nickname: String,
    val goalCnt: Int,
    val todayTime: String?,
    val isNewPhotoVerify: Boolean,
    val profileImage: String
)

data class FriendReportRequestDto(
    val userId: Int,
    val friendId: Int,
    val reason: String,
    val block: Boolean
)

data class FriendRequestsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendRequestsResult>
)

data class FriendRequestsResult(
    val id: Int,
    val nickname: String,
    val goalCnt: Int,
    val todayTime: String?,
    val isNewPhotoVerify: Boolean
)