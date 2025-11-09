package com.example.planup.network.interceptor

import com.example.planup.database.TokenSaver
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp 요청시 Authorization 토큰 처리담당
 */
class AuthInterceptor(
    private val tokenSaver: TokenSaver
) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.header("Authorization") != null || chain.request().url.pathSegments == listOf("users","login")) {
            return chain.proceed(original)
        }

        val builder = original.newBuilder()

        val token = tokenSaver.safeToken()
        if (!token.isNullOrEmpty()) {
            builder.header("Authorization", token)
        }

        return chain.proceed(builder.build())
    }
}