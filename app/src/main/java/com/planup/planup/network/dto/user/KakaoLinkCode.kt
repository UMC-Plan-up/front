package com.planup.planup.network.dto.user

import com.google.gson.annotations.SerializedName

data class KakaoLinkCode(
    @SerializedName("code") val code: String
)
