package com.example.planup.signup.data

data class NicknameCheckResponse(
    val isSuccess: Boolean,
    val code: String?,
    val message: String?,
    val result: Result?
) {
    data class Result(
        val available: Boolean,
        val message: String
    )
}
