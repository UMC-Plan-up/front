package com.example.planup.network.data.user

import com.google.gson.annotations.SerializedName

data class PostInviteCode(
    @SerializedName(value = "inviteCode") val inviteCode: String
)
