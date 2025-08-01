package com.example.planup.signup.data

data class TermsDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TermDetail
)

data class TermDetail(
    val id: Int,
    val content: String
)
