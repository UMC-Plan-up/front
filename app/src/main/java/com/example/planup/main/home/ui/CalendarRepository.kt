package com.example.planup.main.home.ui

import com.example.planup.database.TokenSaver
import com.example.planup.database.checkToken
import com.example.planup.main.goal.item.DailyGoalResult
import com.example.planup.network.ApiResult
import com.example.planup.network.GoalApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val tokenSaver: TokenSaver,
    private val goalApi: GoalApi
) {
    suspend fun loadDailyGoal(date: String): ApiResult<DailyGoalResult> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getDailyGoal(token, date)
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
}


