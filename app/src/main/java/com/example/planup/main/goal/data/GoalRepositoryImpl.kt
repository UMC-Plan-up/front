package com.example.planup.main.goal.data

import android.R.attr.action
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.database.TokenSaver
import com.example.planup.database.UserInfoSaver
import com.example.planup.database.checkToken
import com.example.planup.main.goal.domain.GoalRepository
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.goal.item.MyGoalListResponse
import com.example.planup.network.ApiResult
import com.example.planup.network.GoalApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.adapter.rxjava2.Result.response
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalApi: GoalApi,
    private val tokenSaver: TokenSaver
) : GoalRepository {

    override suspend fun updateGoal(token: String, goalId: Int, request: EditGoalRequest): Boolean {
            try {
                val response = goalApi.editGoal(token = token, goalId = goalId, editGoalRequest = request)

                if (response.isSuccess){
                    Log.d("EditGoalFragment", "성공 메시지: ${response.message}")
                    return true
                } else {
                    val errorMessage = response.message
                    Log.d("EditGoalFragment", "에러 메시지: $errorMessage")
                    return false
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
                } else {
                    Log.e("API", "Other error: ${e.message}", e)
                }
            }
        return false
    }

    override suspend fun fetchMyGoals() =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        goalApi.getMyGoalList(token)
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