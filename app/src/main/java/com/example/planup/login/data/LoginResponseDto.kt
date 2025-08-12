
package com.example.planup.login.data

data class LoginResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginResult
)

data class LoginResult(
    val accessToken: String,
    val nickname: String,
    val profileImgUrl: String
)
