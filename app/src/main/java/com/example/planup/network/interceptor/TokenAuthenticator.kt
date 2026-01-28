package com.example.planup.network.interceptor

import android.util.Log
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val userRepository: dagger.Lazy<UserRepository>
) : Authenticator {
    private val TAG = "TokenAuthenticator"
    private val HEADER_TOKEN = "Authorization"
    private val mutex = Mutex()
    private val MAX_RETRY = 3

    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        mutex.lock()
        val request = response.request
        val retryCount = response.retryCount()
        Log.d(TAG, "토큰 갱신 시도($retryCount)")

        if (request.header(HEADER_TOKEN).isNullOrBlank()) {
            // 헤더에 토큰이 없는 요청인 경우
            return@runBlocking null
        }

        if (retryCount > MAX_RETRY) {
            // 최대 시도 횟수를 넘은 경우
            return@runBlocking null
        }

        val refreshResult = userRepository.get().refreshToken()

        refreshResult.onSuccess {
            return@runBlocking request.newBuilder()
                .removeHeader(HEADER_TOKEN)
                .addHeader(HEADER_TOKEN, it.accessToken)
                .build()
        }.onFailWithMessage {
            Log.e("TAG", "토큰 갱신 요청 실패: it")
            return@runBlocking null
        }

        mutex.unlock()
        null
    }

    private fun Response.retryCount(): Int {
        var curr = this
        var retry = 1

        while(curr.priorResponse != null) {
            curr = curr.priorResponse!!
            retry++
        }

        return retry
    }
}