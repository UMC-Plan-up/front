package com.example.planup.password.data

data class PasswordUpdateResponseDto (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)