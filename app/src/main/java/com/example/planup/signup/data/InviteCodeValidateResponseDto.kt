package com.example.planup.signup.data

data class InviteCodeValidateResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val valid: Boolean,
        val message: String,
        val targetUserNickname: String
    )
}