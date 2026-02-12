package com.planup.planup.network

import com.planup.planup.main.record.data.MessageResponseDto
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface EncourageMessageApi {

    @POST("/api/encourage")
    suspend fun getEncourageMessage(
        @Header("Authorization") token: String,
    ): Response<MessageResponseDto>
}