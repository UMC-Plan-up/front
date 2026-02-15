package com.planup.planup.signup.data

// 요청 Dto
data class InviteCodeValidateRequest(
    val inviteCode: String
)

// 응답 Dto
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
