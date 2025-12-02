package com.example.planup.network

import com.example.planup.login.data.LoginRequest
import com.example.planup.login.data.LoginResponse
import com.example.planup.main.user.data.UserInfoResponse
import com.example.planup.network.data.UserResponse
import com.example.planup.network.data.UsingKakao
import com.example.planup.network.data.WithDraw
import com.example.planup.password.data.ChangeLinkVerifyResponseDto
import com.example.planup.password.data.PasswordChangeEmailRequestDto
import com.example.planup.password.data.PasswordChangeEmailResponseDto
import com.example.planup.password.data.PasswordUpdateRequest
import com.example.planup.password.data.PasswordUpdateResponse
import com.example.planup.signup.data.AlternativeLoginRequest
import com.example.planup.signup.data.AlternativeLoginResponse
import com.example.planup.signup.data.ApiEnvelope
import com.example.planup.signup.data.ApiResponse
import com.example.planup.signup.data.EmailCheckResult
import com.example.planup.signup.data.EmailSendRequestDto
import com.example.planup.signup.data.EmailSendResponseDto
import com.example.planup.signup.data.InviteCodeProcessResponse
import com.example.planup.signup.data.InviteCodeRequest
import com.example.planup.signup.data.InviteCodeResponse
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
import com.example.planup.signup.data.KakaoCompleteRequest
import com.example.planup.signup.data.KakaoCompleteResponse
import com.example.planup.signup.data.KakaoLoginRequest
import com.example.planup.signup.data.KakaoLoginResponse
import com.example.planup.signup.data.NicknameCheckResponse
import com.example.planup.signup.data.ProfileImageResponse
import com.example.planup.signup.data.ResendEmailRequest
import com.example.planup.signup.data.ResendEmailResponse
import com.example.planup.signup.data.SignupRequestDto
import com.example.planup.signup.data.SignupResponseDto
import com.example.planup.signup.data.VerifyLinkResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi : MyPageApi {

    // 회원가입
    @POST("/users/signup")
    suspend fun signup(
        @Body request: SignupRequestDto
    ): Response<SignupResponseDto>

    //로그아웃
    @POST("users/logout")
    suspend fun logout(): Response<UserResponse<String>>

    // 로그인
    @POST("/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    //회원 탈퇴
    @POST("users/withdraw")
    suspend fun withdrawAccount(
        @Body reason: String
    ): Response<UserResponse<WithDraw>>

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

    // 초대코드 처리
    @POST("users/invite-code/process")
    suspend fun processInviteCode(
        @Body body: InviteCodeRequest
    ): Response<InviteCodeProcessResponse>

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

    // 이메일 중복 확인
    @GET("/users/email/check-duplicate")
    suspend fun checkEmailDuplicate(
        @Query("email") email: String
    ): ApiEnvelope<EmailCheckResult>

    // 비밀번호 변경 확인 이메일 발송
    @POST("/users/password/change-email/send")
    suspend fun sendPasswordChangeEmail(
        @Body body: PasswordChangeEmailRequestDto
    ): Response<PasswordChangeEmailResponseDto>

    // 비밀번호 변경 확인 이메일 재발송
    @POST("/users/password/change-email/resend")
    suspend fun resendPasswordChangeEmail(
        @Body body: PasswordChangeEmailRequestDto
    ): Response<PasswordChangeEmailResponseDto>

    // 비밀번호 변경 요청 이메일 링크 클릭 처리
    @GET("/users/password/change-link")
    suspend fun verifyChangeLink(
        @Query("token") token: String
    ): Response<ChangeLinkVerifyResponseDto>

    // 카카오 소셜 인증
    @POST("/users/auth/kakao")
    suspend fun kakaoLogin(
        @Body request: KakaoLoginRequest
    ): Response<KakaoLoginResponse>

    // 카카오 회원가입 완료
    @POST("/users/auth/kakao/complete")
    suspend fun kakaoComplete(
        @Body body: KakaoCompleteRequest
    ): Response<KakaoCompleteResponse>

    // 이메일 인증 대안 - 카카오 로그인
    @POST("/users/auth/email/alternative")
    suspend fun alternativeLogin(
        @Body request: AlternativeLoginRequest
    ): Response<AlternativeLoginResponse>

    // 닉네임 중복 확인
    @GET("users/nickname/check-duplicate")
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): Response<NicknameCheckResponse>

    // 비밀번호 재설정
    @POST("/users/password/change")
    suspend fun changePassword(
        @Body request: PasswordUpdateRequest
    ): Response<PasswordUpdateResponse>

    // 유저 정보 조회
    @GET("/users/info")
    suspend fun getUserInfo(): Response<UserInfoResponse>

    //프로필 이미지 변경
    @Multipart
    @POST("profile/image")
    suspend fun setProfileImage(@Part file: MultipartBody.Part): Response<UserResponse<ProfileImageResponse.Result>>

    //서비스 알림 동의 변경
    @PATCH("mypage/notification/service")
    suspend fun patchNoticeService(): Response<UserResponse<Boolean>>

    //혜택 및 마케팅 동의 변경
    @PATCH("mypage/notification/marketing")
    suspend fun patchNoticeMarketing(): Response<UserResponse<Boolean>>
}

interface MyPageApi {

    //닉네임 수정
    @POST("mypage/profile/nickname")
    suspend fun changeNickname(
        @Body nickname: String
    ): Response<UserResponse<String>>

    //유저 정보 카카오 연동 조회
    @GET("mypage/kakao-account")
    suspend fun getKakaoAccountLink(

    ): Response<UserResponse<UsingKakao>>
}
