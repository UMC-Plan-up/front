package com.example.planup.network

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GoalApi {
    @POST("/goals/create")
    suspend fun createGoal(
        @Body goalCreateRequest: GoalCreateRequest
    ): Response<GoalCreateResponse>
}
