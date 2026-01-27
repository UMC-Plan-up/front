package com.example.planup.network.interceptor

import com.example.planup.database.TokenSaver
import com.example.planup.network.UserApi
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenSaver: TokenSaver,
    private val userApi: dagger.Lazy<UserApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request

        return request
            .newBuilder()
            .build()
    }

}