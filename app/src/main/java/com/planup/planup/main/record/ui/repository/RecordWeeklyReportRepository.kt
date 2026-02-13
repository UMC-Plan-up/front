package com.planup.planup.main.record.ui.repository

import com.planup.planup.main.record.data.DetailWeeklyReportResult
import com.planup.planup.network.ApiResult
import com.planup.planup.network.NotificationApi
import com.planup.planup.network.RecordApi
import com.planup.planup.network.UserApi
import com.planup.planup.network.data.ChallengeFriends
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordWeeklyReportRepository @Inject constructor(
    private val recordApi : RecordApi,
    private val userApi : UserApi,
    private val notiApi: NotificationApi
) {
    suspend fun loadWeeklyReport(
        userId: Int, year: Int, month: Int, week: Int
    ): ApiResult<DetailWeeklyReportResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    recordApi.getWeeklyReports(year, month, week, userId)
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

    suspend fun loadChallengeFriend(userId: Int): ApiResult<List<ChallengeFriends>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    recordApi.showFriends(userId)
                },
                onResponse = {
                    if(it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
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
}