package com.planup.planup.main.record.ui.repository

import com.planup.planup.network.RecordApi
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.planup.planup.network.ApiResult
import com.planup.planup.network.GoalApi
import com.planup.planup.network.UserApi

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