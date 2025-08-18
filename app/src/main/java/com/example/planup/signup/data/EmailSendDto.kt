package com.example.planup.signup.data

// 요청 Dto
data class EmailSendRequestDto(
    val email: String
)

// 응답 Dto
data class EmailSendResponseDto(
    val isSuccess: Boolean,
    val code: String?,
    val message: String?,
    val result: Result?
) {
    data class Result(
        val email: String,
        val message: String,
        val verificationToken: String
    )
}