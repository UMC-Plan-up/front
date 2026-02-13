package com.planup.planup.main.home.ui

import com.planup.planup.main.goal.item.DailyGoalResult
import com.planup.planup.network.ApiResult
import com.planup.planup.network.GoalApi
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val goalApi: GoalApi
) {
    suspend fun loadDailyGoal(date: String): ApiResult<DailyGoalResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getDailyGoal(date)
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


