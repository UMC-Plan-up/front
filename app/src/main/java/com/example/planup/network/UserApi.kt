package com.example.planup.network

import com.example.planup.login.data.LoginRequestDto
import com.example.planup.login.data.LoginResponseDto
import com.example.planup.signup.data.InviteCodeResponse
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
import com.example.planup.signup.data.SignupRequestDto
import com.example.planup.signup.data.SignupResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {

    // 로그인
    @POST("/users/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

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


    @POST("/users/signup")
    suspend fun signup(
        @Body request: SignupRequestDto
    ): Response<SignupResponseDto>
}
