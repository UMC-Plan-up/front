package com.example.planup.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.example.planup.network.ApiResult

class TokenSaver(
    context: Context
) {
    companion object {
        private const val PREF_TOKEN_NAME = "Token"
        private const val TOKEN_KEY = "token"
    }

    private val prefs = context.getSharedPreferences(PREF_TOKEN_NAME, MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit {
            putString(TOKEN_KEY,token)
        }
    }

    fun getToken() : String? {
        return prefs.getString(TOKEN_KEY,null)
    }

    /**
     * Token 값이 존재할 경우 Bearer 여부를 판단해 추가로 반환한다.
     */
    fun safeToken() : String? {
        return getToken()?.let { token ->
            if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
        }
    }
}

suspend inline fun <T> TokenSaver.checkToken(
    onToken: suspend (String) -> ApiResult<T>
): ApiResult<T> {
    val savedToken = safeToken()
    if (savedToken.isNullOrBlank()) {
        //TODO Error When Token is null or blank
        return ApiResult.Error("invalid Token")
    }

    return onToken(savedToken)
}