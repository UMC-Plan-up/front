package com.planup.planup.main.goal.item

data class FriendPhotosResponse (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<FriendPhotoResult>
)
data class FriendPhotoResult(
    val verificationId: Int,
    val goalId: Int,
    val photoImg: String
)