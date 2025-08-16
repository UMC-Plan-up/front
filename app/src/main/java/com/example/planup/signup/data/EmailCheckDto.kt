package com.example.planup.signup.data

data class EmailCheckResult(
    val available: Boolean,
    val message: String
)

data class ApiEnvelope<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)