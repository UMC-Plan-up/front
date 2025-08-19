package com.example.planup.network

import com.example.planup.main.record.adapter.DetailWeeklyReportResponse
import com.example.planup.main.record.adapter.WeeklyReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface WeeklyReportApi {

    // 목표 달성 기록 조회 페이지 데이터 생성
    @GET("/report/reports")
    suspend fun getWeeklyGoalReportRequest(
        @Header("Authorization") token: String
    ): Response<WeeklyReportResponse>

    // 년/을 기준으로 존재하는 리포트 리스트 반환
    @GET("/report/reports")
    suspend fun getMonthlyReports(
        @Header("Authorization") token: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("userId") userId: Int
    ): Response<WeeklyReportResponse>

    // 각 주차별 종합 리포트 반환
    @GET("/report/reports")
    suspend fun getWeeklyReports(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("week") week: Int,
        @Query("userId") userId: Int
    ): Response<DetailWeeklyReportResponse>

}