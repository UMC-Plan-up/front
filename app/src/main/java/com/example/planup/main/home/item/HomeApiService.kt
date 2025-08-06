package com.example.planup.main.home.item

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Response

interface GoalApiService {
    @GET("/goals/my")
    suspend fun getMyGoals(): Response<MyGoalApiResponse>
}