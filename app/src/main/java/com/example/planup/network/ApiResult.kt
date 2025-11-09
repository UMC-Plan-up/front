package com.example.planup.network

import com.example.planup.network.dto.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Fail(val message: String) : ApiResult<Nothing>()
    data class Error(val message: String) : ApiResult<Nothing>()
    data class Exception(val error: Throwable) : ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.onSuccess(
    onSuccessAction: (data: T) -> Unit
): ApiResult<T> {
    if (this is ApiResult.Success) {
        onSuccessAction(this.data)
    }
    return this
}

inline fun <T> ApiResult<T>.onFailWithMessage(
    onFailAction: (message: String) -> Unit
): ApiResult<T> {
    when (this) {
        is ApiResult.Fail -> {
            onFailAction(this.message)
        }

        is ApiResult.Error -> {
            onFailAction(this.message)
        }

        is ApiResult.Exception -> {
            onFailAction(this.error.message ?: "Unknown Error")
        }

        else -> return this
    }
    return this
}

/**
 * @param response API 콜을 통한 Response<T> 응답
 * @param onResponse 응답 성공시 T를 기반으로 ApiResult<R> 결과값 반환
 */
suspend inline fun <T, R> safeResult(
    response: suspend () -> Response<T>,
    onResponse: (T) -> ApiResult<R>
): ApiResult<R> {
    return try {
        val response = response()
        if (response.isSuccessful) {
            val result: T? = response.body()
            if (result != null) {
                onResponse(result)
            } else {
                ApiResult.Error("fail response by body is null")
            }
        } else {
            val error = runCatching {
                val body  = response.errorBody()!!.string()
                val errorResponse = Gson().fromJson(body, ErrorResponse::class.java)
                errorResponse
            }
            if (error.isSuccess) {
                ApiResult.Error(error.getOrNull()!!.message)
            } else {
                ApiResult.Error("fail response by response empty")
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}