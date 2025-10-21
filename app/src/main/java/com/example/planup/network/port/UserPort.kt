package com.example.planup.network.port

import com.example.planup.network.adapter.KakaoLinkAdapter
import com.example.planup.network.data.EmailLink
import com.example.planup.network.data.SignupLink
import com.example.planup.network.data.InviteCodeValidate
import com.example.planup.network.data.KakaoLink
import com.example.planup.network.data.UsingKakao
import com.example.planup.network.data.Login
import com.example.planup.network.data.MyInviteCode
import com.example.planup.network.data.PasswordLink
import com.example.planup.network.data.ProfileImage
import com.example.planup.network.data.Signup
import com.example.planup.network.data.SyncKakao
import com.example.planup.network.data.UserResponse
import com.example.planup.network.data.UserInfo
import com.example.planup.network.data.WithDraw
import com.example.planup.network.dto.user.ChangePassword
import com.example.planup.network.dto.user.LoginDto
import com.example.planup.network.dto.user.EmailForPassword
import com.example.planup.network.dto.user.KakaoLinkCode
import com.example.planup.network.dto.user.SignupDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

@Deprecated(message = "UserApi 로 통일해서 사용")
interface UserPort {

    //내 초대코드 조회
    @GET("users/me/invite-code")
    fun getInviteCode(): Call<UserResponse<MyInviteCode>>
    //유저 정보 조회
    @GET("users/info")
    fun getUserInfo(): Call<UserResponse<UserInfo>>
    //카카오 계정 연동상태 확인
    @GET("mypage/kakao-account")
    fun getKakao(): Call<UserResponse<UsingKakao>>

    //혜택 및 마케팅 동의 변경
    @PATCH("mypage/notification/agree")
    fun patchNoticeAgree(): Call<UserResponse<Boolean>>

    //회원 탈퇴
    @POST("users/withdraw")
    fun withdrawAccount(@Body reason: String ): Call<UserResponse<WithDraw>>
    //회원가입
    @POST("users/signup")
    fun signup(@Body signup: SignupDto): Call<UserResponse<Signup>>
    //비밀번호 변경 시 이메일 인증 발송
    @POST("users/password/change-email/send")
    fun passwordLink(@Body email: EmailForPassword): Call<UserResponse<PasswordLink>>
    //비밀번호 변경 시 이메일 인증 재발송
    @POST("users/password/change-email/resend")
    fun passwordRelink(@Body email: String): Call<UserResponse<PasswordLink>>
    //로그아웃
    @POST("users/logout")
    fun logout(): Call<UserResponse<Boolean>>
    //로그인
    @POST("users/login")
    fun login(@Body loginInfo: LoginDto): Call<UserResponse<Login>>
    //초대코드 실시간 검증
    @POST("users/invite-code/validate")
    fun validateCode(@Body inviteCode: String): Call<UserResponse<InviteCodeValidate>>
    //회원가입 시 이메일 인증 발송
    @POST("users/email/send")
    fun signupLink(@Body email: String): Call<UserResponse<SignupLink>>
    //회원가입 시 이메일 인증 재발송
    @POST("users/email/resend")
    fun signupRelink(@Body email: String): Call<UserResponse<SignupLink>>
    //이메일 변경 인증 메일 발송
    @POST("users/email/change/send")
    fun emailLink(@Body email: String): Call<UserResponse<EmailLink>>
    //이메일 변경 인증 메일 재발송
    @POST("users/email/change/resend")
    fun emailReLink(@Body email: String): Call<UserResponse<EmailLink>>
    //이미지 변경
    @Multipart
    @POST("profile/image")
    fun setProfileImage(@Part file: MultipartBody.Part): Call<UserResponse<ProfileImage>>
    //비밀번호 변경
    @POST("/users/password/change")
    fun changePassword(@Body password: ChangePassword): Call<UserResponse<Boolean>>

    @POST("users/auth/kakao")
    fun syncKakao(@Body code: String): Call<SyncKakao>

    @POST("mypage/kakao-account/link")
    fun linkKakao(@Body code: KakaoLinkCode): Call<UserResponse<KakaoLink>>
}