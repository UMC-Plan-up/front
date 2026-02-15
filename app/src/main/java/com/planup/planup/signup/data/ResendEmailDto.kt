package com.planup.planup.signup.data

// 요청 DTO
data class ResendEmailRequest(
    val email: String
)

// 응답 DTO
data class ResendEmailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
) {
    data class ResultData(
        val email: String,
        val message: String,
        val verificationToken: String
    )
}