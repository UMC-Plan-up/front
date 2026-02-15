package com.planup.planup.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.planup.planup.network.ApiResult

class TokenSaver(
    context: Context
) {
    companion object {
        private const val PREF_TOKEN_NAME = "Token"
        private const val TOKEN_KEY = "token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    private val prefs = context.getSharedPreferences(PREF_TOKEN_NAME, MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit {
            putString(TOKEN_KEY,token)
        }
    }

    fun saveRefreshToken(refreshToken: String?) {
        prefs.edit {
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    fun getToken() : String? {
        return prefs.getString(TOKEN_KEY,null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN_KEY, null)
    }

    fun clearTokens() {
        prefs.edit {
            remove(TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
        }
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