package com.planup.planup.network.dto.user

import com.google.gson.annotations.SerializedName

data class EmailForPassword(
    @SerializedName("email") val email: String,
    @SerializedName("isLoggedIn") val isLoggedIn: Boolean
)
