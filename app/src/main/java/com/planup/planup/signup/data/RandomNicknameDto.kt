package com.planup.planup.signup.data

data class RandomNicknameResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val nickname: String
    )
}