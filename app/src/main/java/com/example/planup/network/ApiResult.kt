package com.example.planup.network

import kotlinx.coroutines.CancellationException
import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Fail(val message: String) : ApiResult<Nothing>()
    data class Error(val message: String) : ApiResult<Nothing>()
    data class Exception(val error: Throwable) : ApiResult<Nothing>()
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
            ApiResult.Error("fail response by response empty")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}