package com.example.planup.signup.data

data class TermsListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<TermItem>
)

data class TermItem(
    val id: Int,
    val summary: String,
    val isRequired: Boolean,
    val order: Int
)
