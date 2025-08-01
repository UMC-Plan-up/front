package com.example.planup.signup.data

data class SignupResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
)

data class ResultData(
    val id: Int,
    val email: String,
    val friendNickname: String
)
