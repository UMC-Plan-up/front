package com.example.planup.main.home.ui

import com.example.planup.database.TokenSaver
import com.example.planup.main.user.data.UserInfoResponse
import com.example.planup.network.ApiResult
import com.example.planup.network.NotificationApi
import com.example.planup.network.UserApi
import com.example.planup.network.dto.notification.NotificationPatchResponse
import com.example.planup.network.dto.notification.NotificationResult
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeAlertRepository @Inject constructor(
    private val notiApi: NotificationApi,
    private val userApi: UserApi
) {
    suspend fun loadNotification(receiverId: Int): ApiResult<List<NotificationResult>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    notiApi.loadNotification(receiverId)
                },
                onResponse = { notificationDto ->
                    if (notificationDto.isSuccess) {
                        val resultList = notificationDto.result
                        ApiResult.Success(resultList)
                    } else {
                        ApiResult.Fail(notificationDto.message)
                    }
                }
            )
        }

    suspend fun loadNotificationType(
        receiverId: Int, type: String
    ): ApiResult<List<NotificationResult>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    notiApi.loadNotificationType(receiverId,type)
                },
                onResponse = { notificationDto ->
                    if (notificationDto.isSuccess) {
                        val resultList = notificationDto.result
                        ApiResult.Success(resultList)
                    } else {
                        ApiResult.Fail(notificationDto.message)
                    }
                }
            )
        }

    suspend fun patchNotification(userId: Int, notificationId: Int): ApiResult<NotificationPatchResponse> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    notiApi.patchNotification(userId, notificationId)
                },
                onResponse = { notificationDto ->
                    if (notificationDto.isSuccess) {
                        ApiResult.Success(notificationDto)
                    } else {
                        ApiResult.Fail(notificationDto.message)
                    }
                }
            )
        }

    suspend fun loadUserInfo(): ApiResult<UserInfoResponse> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.getUserInfo()
                },
                onResponse = { userInfoDto ->
                    if (userInfoDto.isSuccess) {
                        ApiResult.Success(userInfoDto)
                    } else {
                        ApiResult.Fail(userInfoDto.message)
                    }
                }
            )
        }
}