package com.planup.planup.network.dto.user

import com.google.gson.annotations.SerializedName

@Deprecated("LoginRequest 로 통일")
data class LoginDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
