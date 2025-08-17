package com.example.planup.signup.data

// 응답 Dto
data class UserInfo(
    val id: Int,
    val email: String,
    val nickname: String,
    val profileImg: String
)

data class SignupResult(
    val id: Int,
    val email: String,
    val accessToken: String,
    val userInfo: UserInfo
)

data class SignupResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: SignupResult?
)
