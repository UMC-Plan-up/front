package com.example.planup.main.my.data

import com.google.gson.annotations.SerializedName

data class PostInviteCode(
    @SerializedName(value = "inviteCode") val inviteCode: String
)
