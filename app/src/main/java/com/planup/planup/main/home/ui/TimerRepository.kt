package com.planup.planup.main.home.data

import com.planup.planup.database.TokenSaver
import com.planup.planup.database.checkToken
import com.planup.planup.main.goal.item.DateMemoResult
import com.planup.planup.main.goal.item.EditGoalResponse
import com.planup.planup.main.goal.item.FriendsTimerResult
import com.planup.planup.main.goal.item.MemoRequest
import com.planup.planup.main.goal.item.MyGoalListItem
import com.planup.planup.main.goal.item.PostMemoResult
import com.planup.planup.network.ApiResult
import com.planup.planup.network.GoalApi
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.network.data.TimerStartResult
import com.planup.planup.network.data.TimerStopResult
import com.planup.planup.network.data.TodayTotalTimeResult
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepository @Inject constructor(
    private val goalApi: GoalApi,
    private val tokenSaver: TokenSaver
) {
    private val verifyApi = RetrofitInstance.verificationApi

    suspend fun getMyGoalList() : ApiResult<List<MyGoalListItem>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getMyGoalList()
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


    suspend fun getFriendsTimer(goalId: Int) : ApiResult<List<FriendsTimerResult>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getFriendsTimer(goalId)
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



    suspend fun getTodayTotalTime(goalId: Int) : ApiResult<TodayTotalTimeResult> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        verifyApi.getTodayTotalTime(token, goalId)
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


    suspend fun startTimer(goalId: Int) : ApiResult<TimerStartResult> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        verifyApi.postTimerStart(token, goalId)
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

    suspend fun stopTimer(timerId: Int): ApiResult<TimerStopResult> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        verifyApi.putTimerStop(token, timerId)
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

    suspend fun saveMemo(goalId: Int, date: String, memo: String): ApiResult<PostMemoResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.saveMemo(
                        goalId,
                        MemoRequest(memo, date, memo.isEmpty(), memo.trim())
                    )
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

    suspend fun getDateMemo(goalId: Int, date: String): ApiResult<DateMemoResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getDateMemo(goalId, date)
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

    suspend fun getEditGoal(goalId: Int): ApiResult<EditGoalResponse> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                        goalApi.getEditGoal(goalId)
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
