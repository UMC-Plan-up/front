package com.example.planup.network

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalListResponseDto
import com.example.planup.main.goal.item.DailyAchievementResponse
import com.example.planup.main.goal.item.DailyGoalResponse
import com.example.planup.main.goal.item.FriendPhotosResponse
import com.example.planup.main.goal.item.TotalAchievementResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GoalApi {
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

    @GET("/community/daily-achievement")
    suspend fun getDailyAchievement(
        @Header("Authorization") token: String,
        @Query("targetDate") targetDate: String
    ): DailyAchievementResponse

    @GET("/community/{goalId}/total-achievement")
    suspend fun getTotalAchievement(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int
    ): TotalAchievementResponse

    @GET("/goals/friend/{friendId}/goal/{goalId}/photos")
    suspend fun getFriendPhotos(
        @Header("Authorization") token: String,
        @Path("friendId") friendId: Int,
        @Path("goalId") goalId: Int,
        @Query("userId") userId: Int
    ): FriendPhotosResponse
}
