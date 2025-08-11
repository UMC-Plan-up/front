package com.example.planup.network

import com.example.planup.login.data.LoginRequestDto
import com.example.planup.login.data.LoginResponseDto
import com.example.planup.signup.data.ApiResponse
import com.example.planup.signup.data.EmailSendRequestDto
import com.example.planup.signup.data.EmailSendResponseDto
import com.example.planup.signup.data.InviteCodeResponse
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
import com.example.planup.signup.data.ResendEmailRequest
import com.example.planup.signup.data.ResendEmailResponse
import com.example.planup.signup.data.SignupRequestDto
import com.example.planup.signup.data.SignupResponseDto
import com.example.planup.signup.data.VerifyLinkResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    // 회원가입
    @POST("/users/signup")
    suspend fun signup(
        @Body request: SignupRequestDto
    ): Response<SignupResponseDto>

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

    // 이메일 인증 메일 발송
    @POST("/users/email/send")
    suspend fun sendEmail(
        @Body body: EmailSendRequestDto
    ): Response<EmailSendResponseDto>

    // 이메일 인증 메일 재발송
    @POST("/users/email/resend")
    suspend fun resendEmail(
        @Body body: ResendEmailRequest
    ): Response<ResendEmailResponse>

    // 이메일 인증 링크 검증
    @GET("/users/email/verify-link")
    suspend fun verifyEmailLink(
        @Query("token") token: String
    ): Response<ApiResponse<VerifyLinkResult>>
}
