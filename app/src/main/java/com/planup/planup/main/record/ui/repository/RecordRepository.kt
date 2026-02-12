package com.planup.planup.main.record.ui.repository

import com.planup.planup.main.record.data.WeeklyReportResult
import com.planup.planup.network.ApiResult
import com.planup.planup.network.NotificationApi
import com.planup.planup.network.RecordApi
import com.planup.planup.network.UserApi
import com.planup.planup.network.dto.notification.NotificationResult
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(
    private val recordApi : RecordApi,
    private val userApi : UserApi,
    private val notiApi: NotificationApi
) {
    suspend fun loadWeeklyGoalReport(userId: Int): ApiResult<WeeklyReportResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    recordApi.getWeeklyGoalReportRequest(userId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    suspend fun loadMonthlyReport(userId: Int, year: Int, month: Int): ApiResult<Int> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    recordApi.getMonthlyReports(year, month, userId)
                },
                onResponse = { response ->
                    if(response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    suspend fun getUserInfo() =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.getUserInfo()
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

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


}