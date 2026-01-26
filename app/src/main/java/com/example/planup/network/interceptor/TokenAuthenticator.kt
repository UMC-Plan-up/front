package com.example.planup.network.interceptor

import com.example.planup.database.TokenSaver
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenSaver: TokenSaver,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request

        return request
            .newBuilder()
            .build()
    }

}