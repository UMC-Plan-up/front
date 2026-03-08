package com.planup.planup.network.dto

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

data class ErrorResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T? = null
)

inline fun <reified T> Response<*>.getErrorResponse(): ErrorResponse<T>? {
    val errorBody = this.errorBody()?.string() ?: return null

    return try {
        val type = object : TypeToken<ErrorResponse<T>>() {}.type
        Gson().fromJson(errorBody, type)
    } catch (e: Exception) {
        null
    }
}