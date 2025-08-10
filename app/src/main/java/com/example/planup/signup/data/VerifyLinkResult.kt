package com.example.planup.signup.data

data class VerifyLinkResult(
    val verified: Boolean,
    val email: String,
    val message: String,
    val deepLinkUrl: String,
    val token: String
)