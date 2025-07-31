package com.example.planup.network

import com.example.planup.signup.data.SignupRequestDto
import com.example.planup.signup.data.SignupResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupApi {
    @POST("/users/signup")
    suspend fun signup(
        @Body request: SignupRequestDto
    ): SignupResponseDto
}