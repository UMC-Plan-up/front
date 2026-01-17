package com.example.planup.util

import android.content.Context
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object KakaoServiceHandler {
    private const val LOG_TAG = "KakaoServiceHandler"
    private val authCodeClient get() = AuthCodeClient.instance
    private val userApiClient get() = UserApiClient.instance
    private val shareClient get() = ShareClient.instance

    /**
     * 카카오 로그인으로 인가코드를 받아온다
     *
     * @param context
     * @return 인가코드
     */
    suspend fun getAuthorizeCode(
        context: Context
    ): Result<String> = safeRunCatching {
        suspendCancellableCoroutine { continuation ->
            val authorizeWithKakaoAccountCallback: (String?, Throwable?) -> Unit = { code, error ->
                if (code != null) {
                    continuation.resume(code)
                }
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        continuation.resumeWithException(KakaoHandlerError.CancelledByUser)
                    } else {
                        continuation.resumeWithException(KakaoHandlerError.UnHandledError(error))
                    }
                }
            }

            if (authCodeClient.isKakaoTalkLoginAvailable(context)) {
                // 카카오톡이 설치 된 경우 카카오톡 사용
                authCodeClient.authorizeWithKakaoTalk(context) { code, error ->
                    if (code != null) {
                        continuation.resume(code)
                    }
                    if (error != null) {
                        if (error is AuthError && error.statusCode == 302) {
                            // 카카오톡과 계정이 연결되지 않은 경우 계정으로 로그인
                            authCodeClient.authorizeWithKakaoAccount(
                                context = context,
                                callback = authorizeWithKakaoAccountCallback
                            )
                        } else if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resumeWithException(KakaoHandlerError.CancelledByUser)
                        } else {
                            continuation.resumeWithException(KakaoHandlerError.UnHandledError(error))
                        }
                    }
                }
            } else {
                // 카카오톡이 설치되어 있는 경우 계정으로 로그인
                authCodeClient.authorizeWithKakaoAccount(
                    context = context,
                    callback = authorizeWithKakaoAccountCallback
                )
            }
        }
    }

    /**
     * 카카오 SDK 요청 오류
     *
     * 상황별 오류 대응을 위해 작성
     */
    sealed class KakaoHandlerError(
        message: String? = null,
        override val cause: Throwable? = null
    ) : Exception(message, cause) {

        // 핸들링되지 않은 오류
        class UnHandledError(override val cause: Throwable) : KakaoHandlerError(cause = cause)

        // 유저가 취소 버튼을 누른 경우 발생
        data object CancelledByUser : KakaoHandlerError() {
            private fun readResolve(): Any = CancelledByUser
        }

    }
}