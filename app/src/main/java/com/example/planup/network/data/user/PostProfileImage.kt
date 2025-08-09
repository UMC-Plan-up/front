package com.example.planup.network.data.user

import com.google.gson.annotations.SerializedName

data class PostProfileImage(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Image?
)

data class Image(
    @SerializedName(value = "imageUrl") val imageUrl: String
)
