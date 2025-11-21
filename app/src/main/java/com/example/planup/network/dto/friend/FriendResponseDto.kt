package com.example.planup.network.dto.friend

data class FriendResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FriendResult
) {
    data class FriendResult(
        val cnt: Int,
        val friendInfoSummaryList: List<FriendInfo>
    )
}


data class FriendInfo(
    val id: Int,
    val nickname: String,
    val goalCnt: Int,
    val todayTime: String?,
    val isNewPhotoVerify: Boolean,
    val profileImage: String?
) {
    fun getStatusString(): String = StringBuilder().apply {
        append("${goalCnt}개의 목표 진행 중")
        todayTime?.let { append(" · 오늘 $it") }
        if (isNewPhotoVerify) append(" · 새 사진 인증")
    }.toString()
}

data class FriendReportRequestDto(
    val friendId: Int,
    val reason: String,
    val block: Boolean
)

data class FriendRequestsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendInfo>
)