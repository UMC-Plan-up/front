package com.example.planup.network

import com.example.planup.main.record.data.ApiResponseListInteger
import com.example.planup.main.record.data.DetailWeeklyReportResponse
import com.example.planup.main.record.data.WeeklyReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordApi {
    @GET("/report/reports")
    suspend fun getWeeklyGoalReportRequest(
        @Query("userId") userId: Int
    ): Response<WeeklyReportResponse>

    // 월간(주차 리스트)
    @GET("/report/reports/{year}/{month}")
    suspend fun getMonthlyReports(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("userId") userId: Int
    ): Response<ApiResponseListInteger>

    // 주간 상세 리포트
    @GET("/report/reports/{year}/{month}/{week}")
    suspend fun getWeeklyReports(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("week") week: Int,
        @Query("userId") userId: Int
    ): Response<DetailWeeklyReportResponse>
}