package com.planup.planup.network.dto

data class ErrorResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)