package com.example.planup.network

import com.example.planup.password.data.PasswordCheckResponseDto
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface PasswordApi {
    @POST("/mypage/profile/password")
    suspend fun verifyPassword(
        @Query("userId") userId: Long,
        @Query("password") password: String
    ): Response<PasswordCheckResponseDto>
}
