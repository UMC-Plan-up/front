package com.example.planup.network

import com.example.planup.password.data.PasswordUpdateRequest
import com.example.planup.password.data.PasswordUpdateResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface PasswordApi {
    @POST("/users/password/change")
    suspend fun updatePassword(
        @Query("password") password: PasswordUpdateRequest
    ): Response<PasswordUpdateResponse>
}
