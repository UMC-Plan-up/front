package com.example.planup.network.dto.friend

data class BaseResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)
