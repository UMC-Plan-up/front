package com.example.planup.password.data

data class PasswordResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean  // true: 비밀번호 일치, false: 불일치
)