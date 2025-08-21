package com.example.planup.main.goal.item

import com.example.planup.main.goal.data.MyGoalListDto
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializer

data class ApiResponseListMyGoalListDto(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: MyGoalListDto
)
