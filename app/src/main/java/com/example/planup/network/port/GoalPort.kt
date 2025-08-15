package com.example.planup.network.port

import com.example.planup.main.goal.data.ApiResponseListMyGoalListDto
import retrofit2.Call
import retrofit2.http.GET

interface GoalPort {
    @GET("/goals/mygoal/list")
    fun getMyGoals(): Call<ApiResponseListMyGoalListDto>
}