package com.planup.planup.network.dto.user

import com.google.gson.annotations.SerializedName

data class ChangePassword(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String
)
