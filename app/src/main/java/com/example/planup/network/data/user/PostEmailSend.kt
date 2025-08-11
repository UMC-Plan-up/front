package com.example.planup.network.data.user

import com.google.gson.annotations.SerializedName

data class PostEmailSend(
    @SerializedName("email") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: EmailSend
)
data class EmailSend(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)