package com.planup.planup.signup.data

// 요청 DTO
data class InviteCodeRequest(
    val inviteCode: String
)

// 응답 DTO
data class InviteCodeProcessResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ProcessResult
)

data class ProcessResult(
    val friendNickname: String,
    val message: String,
    val success: Boolean
)
