package com.example.planup.network

import com.example.planup.signup.data.InviteCodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface InviteCodeApi {
    @GET("users/me/invite-code")
    suspend fun getInviteCode(
        @Header("Authorization") token: String
    ): Response<InviteCodeResponse>
}
