package com.example.planup.signup.data

import com.google.gson.annotations.SerializedName

data class Agreement(
    @SerializedName("termsId") val termsId: Int,
    @SerializedName("isAgreed") val isAgreed: Boolean
)

// 요쳥 Dto
data class SignupRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("passwordCheck") val passwordCheck: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("profileImg") val profileImg: String?,
    @SerializedName("agreements") val agreements: List<Agreement>
)
