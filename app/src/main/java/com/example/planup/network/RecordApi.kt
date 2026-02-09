package com.example.planup.network

import com.example.planup.main.record.data.ApiResponseListInteger
import com.example.planup.main.record.data.BadgeListResponse
import com.example.planup.main.record.data.DetailWeeklyReportResponse
import com.example.planup.main.record.data.WeeklyGoalReportResponse
import com.example.planup.main.record.data.WeeklyReportResponse
import com.example.planup.network.data.ChallengeFriends
import com.example.planup.network.data.ChallengeInfo
import com.example.planup.network.data.ChallengeResponse
import com.example.planup.network.data.ChallengeResponseNoResult
import com.example.planup.network.data.ChallengeResult
import com.example.planup.network.dto.challenge.ChallengeDto
import com.example.planup.network.dto.challenge.RepenaltyDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    //챌린지 정보 조회

    //챌린지에서 친구 조회
    @GET("challenges/friends")
    fun showFriends(
        @Query("userId") userId: Int
    ): Response<ChallengeResponse<List<ChallengeFriends>>>

    @GET("/badges/list")
    suspend fun getBadgeList(): Response<BadgeListResponse>

    @GET("/report/goal/{goalReportId}")
    suspend fun getWeeklyGoalReport(
        @Query("userId") userId: Int,
        @Path("goalReportId") goalReportId: Int
    ): Response<WeeklyGoalReportResponse>
}