package com.example.planup.password.data

import com.google.gson.annotations.SerializedName

// 비밀번호 변경 확인 이메일 발송 요청 DTO
data class PasswordChangeEmailRequestDto(
    val email: String,
    val isLoggedIn: Boolean = false
)


// 비밀번호 변경 확인 이메일 발송/재발송 응답 DTO
data class PasswordChangeEmailResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: EmailResult
)

// 발송/재발송 결과 데이터
data class EmailResult(
    val email: String,
    val message: String,
    val verificationToken: String
)

// 비밀번호 변경 링크 검증 응답 DTO
data class ChangeLinkVerifyResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ChangeLinkResult
)

// 비밀번호 변경 링크 검증 결과 데이터
data class ChangeLinkResult(
    val verified: Boolean,
    val email: String,
    val message: String,
    val deepLinkURL: String,
    val token: String
)

// 비밀번호 재설정 요청 Dto
data class PasswordChangeRequest(
    val newPassword: String
)

// 비밀번호 재설정 응답 Dto
data class PasswordUpdateResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)
