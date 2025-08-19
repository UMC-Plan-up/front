package com.example.planup.signup.data

data class Agreement(
    val termsId: Int,
    val isAgreed: Boolean
)

// 요쳥 Dto
data class SignupRequestDto(
    val email: String,
    val password: String,
    val passwordCheck: String,
    val nickname: String,
    val profileImg: String,
    val agreements: List<Agreement>
)
