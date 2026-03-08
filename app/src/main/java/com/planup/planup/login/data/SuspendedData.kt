package com.planup.planup.login.data

import com.google.gson.annotations.SerializedName

data class SuspendedData(
    @SerializedName("sanctionStatus") val sanctionStatus: String?,
    @SerializedName("sanctionEndAt") val sanctionEndAt: String?,
    @SerializedName("sanctionReason") val sanctionReason: String?,
    @SerializedName("sanctionDetailReason") val sanctionDetailReason: String?,
    @SerializedName("reportCount") val reportCount: Int?,
)