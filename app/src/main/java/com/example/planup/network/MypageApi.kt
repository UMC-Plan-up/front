package com.example.planup.network

import com.example.planup.password.data.PasswordUpdateResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface PasswordApi {
    @POST("/mypage/profile/password/update")
    suspend fun updatePassword(
        @Query("password") password: String
    ): Response<PasswordUpdateResponse>
}
