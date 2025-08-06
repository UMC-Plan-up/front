package com.example.planup.main.friend.data

data class BaseResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)
