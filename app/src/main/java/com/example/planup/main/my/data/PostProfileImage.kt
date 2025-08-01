package com.example.planup.main.my.data

import com.google.gson.annotations.SerializedName

<<<<<<< HEAD
data class PostProfileImage(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Image?
)
data class Image(
=======
data class GetProfileImage(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: Result?
)
data class Result(
>>>>>>> e8986d8 ([feat] 네트워크 구현)
    @SerializedName(value = "imageUrl") val imageUrl: String
)
