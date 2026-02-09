package com.example.planup.main.record.ui.repository

import com.example.planup.network.RecordApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.planup.network.ApiResult
import com.example.planup.network.GoalApi
import com.example.planup.network.UserApi

class RecordGoalReportRepository @Inject constructor(
    private val recordApi: RecordApi,
    private val goalApi: GoalApi,
    private val userApi: UserApi
){
    suspend fun loadWeeklyGoalReport(userId: Int, goalReportId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    recordApi.getWeeklyGoalReport(userId, goalReportId)
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

    suspend fun loadGoalPhotos(goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getGoalPhotos(goalId)
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
}