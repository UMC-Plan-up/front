package com.example.planup.main.goal.data

import android.R.attr.action
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.database.UserInfoSaver
import com.example.planup.main.goal.domain.GoalRepository
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.main.goal.ui.EditGoalCompleteFragment
import com.example.planup.network.GoalApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.adapter.rxjava2.Result.response
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalApi: GoalApi,
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
    }
}