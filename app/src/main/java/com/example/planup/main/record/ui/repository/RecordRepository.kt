package com.example.planup.main.record.ui.repository

import androidx.compose.ui.platform.DisableContentCapture
import com.example.planup.database.UserInfoSaver
import com.example.planup.main.record.data.ApiResponseListInteger
import com.example.planup.main.record.data.WeeklyReportResult
import com.example.planup.network.ApiResult
import com.example.planup.network.UserApi
import com.example.planup.network.WeeklyReportApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecordRepository @Inject constructor(
    private val reportApi : WeeklyReportApi,
    private val userApi : UserApi
) {
    suspend fun loadWeeklyGoalReport(userId: Int): ApiResult<WeeklyReportResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    reportApi.getWeeklyGoalReportRequest(userId)
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
                    reportApi.getMonthlyReports(year, month, userId)
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
}