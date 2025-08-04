package com.example.planup.signup.data

data class Agreement(
    val termsId: Int,
    val isAgreed: Boolean
)

data class SignupRequestDto(
    val email: String,
    val password: String,
    val passwordCheck: String,
    val nickname: String,
    val inviteCode: String,
    val profileImg: String,
    val agreements: List<Agreement>
)