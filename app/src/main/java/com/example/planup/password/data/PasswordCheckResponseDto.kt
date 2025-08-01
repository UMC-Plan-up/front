package com.example.planup.password.data

data class PasswordCheckResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)
