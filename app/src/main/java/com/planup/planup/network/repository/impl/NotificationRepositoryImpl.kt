package com.planup.planup.network.repository.impl

import com.google.firebase.messaging.FirebaseMessaging
import com.planup.planup.database.TokenSaver
import com.planup.planup.network.ApiResult
import com.planup.planup.network.NotificationApi
import com.planup.planup.network.dto.notification.UpdateDeviceTokenRequest
import com.planup.planup.network.repository.NotificationRepository
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationRepository {
    override suspend fun getFcmToken(): String? {
        suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        continuation.resume(null)
                    }

                    continuation.resume(task.result)
                }
        }
        return null
    }

    override suspend fun updateFcmToken(): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            getFcmToken()?.let {
                updateFcmToken(it)
            } ?: ApiResult.Fail("Fcm 토큰 로딩 실패")
        }


    override suspend fun updateFcmToken(token: String): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    notificationApi.updateDeviceToken(UpdateDeviceTokenRequest(token))
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun removeFcmToken(): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            getFcmToken()?.let {
                removeFcmToken(it)
            } ?: ApiResult.Fail("Fcm 토큰 로딩 실패")
        }

    override suspend fun removeFcmToken(token: String): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    notificationApi.removeDeviceToken(token)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }
}