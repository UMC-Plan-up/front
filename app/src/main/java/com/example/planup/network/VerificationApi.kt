package com.example.planup.network

import com.example.planup.network.data.TimerStartResponse
import com.example.planup.network.data.TimerStopResponse
import com.example.planup.network.data.TodayTotalTimeResponse
import com.example.planup.network.data.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface VerificationApi {
    @GET("/verification/timer/today-total")
    suspend fun getTodayTotalTime(
        @Header("Authorization") token: String,
        @Query("GoalId") goalId: Int
    ): TodayTotalTimeResponse

    @POST("verification/timer/start")
    suspend fun postTimerStart(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): TimerStartResponse

    @PUT("verification/timer/stop/{timerId}")
    suspend fun putTimerStop(
        @Header("Authorization") token: String,
        @Path("timerId") timerId: Int
    ): TimerStopResponse

    @Multipart
    @POST("verification/photo/upload")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int,
        @Part photoFile: MultipartBody.Part
    ): UploadResponse
}