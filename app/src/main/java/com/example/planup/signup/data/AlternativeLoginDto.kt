package com.example.planup.signup.data

data class AlternativeLoginRequest(
    val code: String
)

data class AlternativeLoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
) {
    data class ResultData(
        val tempUserId: String?,
        val accessToken: String?,
        val userInfo: UserInfo?,
        val newUser: Boolean
    )

    data class UserInfo(
        val id: Int,
        val email: String,
        val nickname: String,
        val profileImg: String
    )
}