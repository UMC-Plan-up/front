package com.example.planup.network

import com.example.planup.login.data.LoginRequestDto
import com.example.planup.login.data.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("/users/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>
}