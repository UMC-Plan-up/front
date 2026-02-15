package com.planup.planup.network.dto.challenge

import com.google.gson.annotations.SerializedName

data class RepenaltyDto(
    @SerializedName("id") val id: Int,
    @SerializedName("penalty") val penalty: String,
    @SerializedName("friendIdList") val friendIdList: List<Long>
)
