package com.planup.planup.network.dto.term

import com.google.gson.annotations.SerializedName

data class TermsDetailResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: TermDetail
)

data class TermDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String
)
