package com.example.planup.network

import com.example.planup.main.record.data.MessageResponseDto
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface EncourageMessageApi {
    @POST("/api/courage")
    suspend fun getEncourageMessage(
        @Header("Authorization") token: String,
    ): Response<MessageResponseDto>
}