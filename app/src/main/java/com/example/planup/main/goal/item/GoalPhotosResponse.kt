package com.example.planup.main.goal.item

data class GoalPhotosResponse (
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<GoalPhotoResult>
)
data class GoalPhotoResult (
    val verificationId: Int,
    val goalId: Int,
    val photoImg: String
)