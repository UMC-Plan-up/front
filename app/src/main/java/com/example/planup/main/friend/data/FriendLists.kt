package com.example.planup.main.friend.data

data class FriendLists(
    val id: Int,
    val nickname: String,
    val goalCnt: Int,
    val todayTime: String,
    val isNewPhotoVerify: Boolean
)

data class FriendListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendListResult>
)

data class FriendListResult(
    val cnt: Int,
    val friendInfoSummaryList: List<FriendInfo>
)

data class FriendInfo(
    val id: Int,
    val nickname: String,
    val goalCnt: Int,
    val todayTime: String,
    val isNewPhotoVerify: Boolean
)