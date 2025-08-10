package com.example.planup.network

import com.example.planup.main.record.adapter.ApiResponseGoalReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GoalReportApi {

    // 주간 목표 리스트 조회
    @GET("/report/goal/{goalReportId}")
    suspend fun getGoalListRequests(
        @Header("Authorization") token: String,
        @Path("goalReportId") goalReportid: Int,
        @Query("userId") userId: Int
    ): Response<ApiResponseGoalReportResponse>
}