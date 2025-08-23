package com.example.planup.network

import com.example.planup.main.record.data.ApiResponseListInteger
import com.example.planup.main.record.data.DetailWeeklyReportResponse
import com.example.planup.main.record.data.WeeklyReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface WeeklyReportApi {

    // 주간 페이지(배지/알림/응원)
    @GET("/report/reports")
    suspend fun getWeeklyGoalReportRequest(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ): Response<WeeklyReportResponse>

    // 월간(주차 리스트)
    @GET("/report/reports/{year}/{month}")
    suspend fun getMonthlyReports(
        @Header("Authorization") token: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("userId") userId: Int
    ): Response<ApiResponseListInteger>

    // 주간 상세 리포트
    @GET("/report/reports/{year}/{month}/{week}")
    suspend fun getWeeklyReports(
        @Header("Authorization") token: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("week") week: Int,
        @Query("userId") userId: Int
    ): Response<DetailWeeklyReportResponse>
}