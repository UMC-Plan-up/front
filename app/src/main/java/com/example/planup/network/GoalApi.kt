package com.example.planup.network

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalListResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoalApi {
    @POST("/goals/create")
    suspend fun createGoal(
        @Body goalCreateRequest: GoalCreateRequest
    ): Response<GoalCreateResponse>

    // 카테고리별 목표 조회 API
    @GET("/goals/create/list")
    suspend fun getGoalsByCategory(
        @Query("goalCategory") goalCategory: String
    ): Response<GoalListResponseDto>
}
