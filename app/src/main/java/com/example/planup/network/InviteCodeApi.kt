package com.example.planup.network

import com.example.planup.signup.data.InviteCodeResponse
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface InviteCodeApi {

    // 내 초대코드 조회
    @GET("users/me/invite-code")
    suspend fun getInviteCode(
        @Header("Authorization") token: String
    ): Response<InviteCodeResponse>

    // 초대코드 실시간 검증
    @POST("/users/invite-code/validate")
    suspend fun validateInviteCode(
        @Body request: InviteCodeValidateRequest
    ): Response<InviteCodeValidateResponse>
}
