package com.planup.planup.main.record.data

import com.google.gson.annotations.SerializedName

data class MessageResponseDto(
    @SerializedName("message") val message: String
)
