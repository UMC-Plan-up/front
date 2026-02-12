package com.planup.planup.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object KakaoServiceHandler {
    private const val LOG_TAG = "KakaoServiceHandler"
    private val authCodeClient get() = AuthCodeClient.instance
    private val userApiClient get() = UserApiClient.instance
    private val shareClient get() = ShareClient.instance

    // 초대코드 공유 템플릿
    private const val SHARE_INVITE_TEMPLATE_CODE = 125174L

    /**
     * 카카오 로그인으로 인가코드를 반환한다
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

    suspend fun getTokenWithUser(
        context: Context
    ): Result<Pair<OAuthToken, User?>> = safeRunCatching {
        suspendCancellableCoroutine { continuation ->
            val loginWithKakaoTalkCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if(token != null) {
                    userApiClient.me { user, error ->
                        continuation.resume(token to user)
                    }
                }
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        continuation.resumeWithException(KakaoHandlerError.CancelledByUser)
                    } else {
                        continuation.resumeWithException(KakaoHandlerError.UnHandledError(error))
                    }
                }
            }

            if (userApiClient.isKakaoTalkLoginAvailable(context)) {
                // 카카오톡이 설치 된 경우 카카오톡 사용
                userApiClient.loginWithKakaoTalk(context) { token, error ->
                    if (token != null) {
                        userApiClient.me { user, error ->
                            continuation.resume(token to user)
                        }
                    }
                    if (error != null) {
                        if (error is AuthError && error.statusCode == 302) {
                            // 카카오톡과 계정이 연결되지 않은 경우 계정으로 로그인
                            userApiClient.loginWithKakaoAccount(
                                context = context,
                                callback = loginWithKakaoTalkCallback
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
                userApiClient.loginWithKakaoAccount(
                    context = context,
                    callback = loginWithKakaoTalkCallback
                )
            }
        }
    }

    /**
     * 초대코드 공유 인텐트를 반환한다
     *
     * @param context
     * @param inviteCode 공유할 초대코드
     * @param nickname 사용자 닉네임
     * @return 카카오톡 공유하기 Intent
     */
    suspend fun shareInviteCode(
        context: Context,
        inviteCode: String,
        nickname: String
    ): Result<Intent> = safeRunCatching {
        suspendCancellableCoroutine { continuation ->
            shareClient.shareCustom(
                context = context,
                templateId = SHARE_INVITE_TEMPLATE_CODE,
                templateArgs = mapOf(
                    "CODE" to inviteCode,
                    "NAME" to nickname
                )
            ) { result, error ->
                if (result != null) {
                    Log.d(LOG_TAG, "초대코드 공유 성공| warning message: ${result.warningMsg}")
                    continuation.resume(result.intent)
                }
                if (error != null) {
                    Log.e(LOG_TAG, "초대코드 공유 실패| error: $error")
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        continuation.resumeWithException(KakaoHandlerError.CancelledByUser)
                    } else {
                        continuation.resumeWithException(KakaoHandlerError.UnHandledError(error))
                    }
                }
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