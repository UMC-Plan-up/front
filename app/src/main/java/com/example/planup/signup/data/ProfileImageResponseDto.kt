package com.example.planup.signup.data

data class ProfileImageResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val imageUrl: String
    )
}