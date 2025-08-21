package com.example.planup.main.goal.adapter

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalListResponseDto
import com.example.planup.main.goal.data.GoalEditResponse
import com.example.planup.main.goal.item.DailyGoalResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GoalApi {

    // 목표 삭제 API
    @DELETE("/goals/{goalId}")
    suspend fun deleteGoal(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): Response<GoalEditResponse>

    // 목표 공개 & 비공개 API
    @PATCH("/goals/{goalId}/public")
    suspend fun setGoalPublic(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): Response<GoalEditResponse>

    // 목표 활성 & 비활성화 API
    @PATCH("/goals/{goalId}/active")
    suspend fun setGoalActive(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): Response<GoalEditResponse>

    @POST("/goals/create")
    suspend fun createGoal(
        @Header("Authorization") token: String,
        @Body goalCreateRequest: GoalCreateRequest
    ): Response<GoalCreateResponse>

    // 카테고리별 목표 조회 API
    @GET("/goals/create/list")
    suspend fun getGoalsByCategory(
        @Query("goalCategory") goalCategory: String
    ): Response<GoalListResponseDto>

    @GET("/goals/daily/{date}")
    suspend fun getDailyGoal(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): DailyGoalResponse
}
