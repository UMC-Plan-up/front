package com.example.planup.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Fail(val message: String) : ApiResult<Nothing>()
    data class Error(val message: String) : ApiResult<Nothing>()
    data class Exception(val error: Throwable) : ApiResult<Nothing>()
}