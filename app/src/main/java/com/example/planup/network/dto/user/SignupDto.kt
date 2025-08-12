package com.example.planup.network.dto.user

import com.google.gson.annotations.SerializedName

data class SignupDto(
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "password") val password: String,
    @SerializedName(value = "passwordCheck") val passwordCheck: String,
    @SerializedName(value = "nickname") val nickname: String,
    @SerializedName(value = "inviteCode") val inviteCode: String,
    @SerializedName(value = "profileImg") val profileImg: String,
    @SerializedName(value = "agreements") val agreements: Agreements?
)
data class Agreements(
    @SerializedName(value = "termsId") val termsId: Int,
    @SerializedName(value = "isAgreed") val isAgreed: Boolean
)