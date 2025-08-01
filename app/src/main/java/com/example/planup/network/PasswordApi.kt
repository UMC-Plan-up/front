package com.example.planup.network

import com.example.planup.password.data.PasswordResponseDto
import com.example.planup.password.data.PasswordUpdateResponseDto
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface PasswordApi {

    @POST("/mypage/profile/password")
    suspend fun verifyPassword(
        @Query("userId") userId: Long,
        @Query("password") password: String
    ): Response<PasswordResponseDto>

    // 비밀번호 변경
    @POST("/mypage/profile/password/update")
    suspend fun updatePassword(
        @Query("userId") userId: Long,
        @Query("password") password: String
    ): Response<PasswordUpdateResponseDto>
}