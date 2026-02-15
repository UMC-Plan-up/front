package com.planup.planup.network.dto.term

import com.google.gson.annotations.SerializedName

data class TermsListResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: TermsResult
)

data class TermsResult(
    @SerializedName("termsList") val termsList: List<TermItem>
)

data class TermItem(
    @SerializedName("id") val id: Int,
    @SerializedName("summary") val summary: String,
    @SerializedName("isRequired") val isRequired: Boolean,
    @SerializedName("order") val order: Int
)
