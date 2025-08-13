package com.example.planup.network.port

import com.example.planup.network.data.SignupLink
import com.example.planup.network.data.InviteCodeValidate
import com.example.planup.network.data.KakaoAccount
import com.example.planup.network.data.Login
import com.example.planup.network.data.MyInviteCode
import com.example.planup.network.data.PasswordLink
import com.example.planup.network.data.ProfileImage
import com.example.planup.network.data.Signup
import com.example.planup.network.data.UserResponse
import com.example.planup.network.data.UserInfo
import com.example.planup.network.data.WithDraw
import com.example.planup.network.dto.user.LoginDto
import com.example.planup.network.dto.user.SignupDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserPort {

    //내 초대코드 조회
    @GET("users/me/invite-code")
    fun getInviteCode(): Call<UserResponse<MyInviteCode>>
    //유저 정보 조회
    @GET("users/info")
    fun getUserInfo(): Call<UserResponse<UserInfo>>
    //카카오 계정 연동상태 확인
    @GET("mypage/kakao-account")
    fun getKakao(): Call<UserResponse<KakaoAccount>>

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
    fun passwordLink(@Body email: String): Call<UserResponse<PasswordLink>>
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
    //이미지 변경
    @POST("profile/image")
    fun setProfileImage(@Body file: String): Call<UserResponse<ProfileImage>>
    //비밀번호 변경
    @POST("mypage/profile/password/update")
    fun changePassword(@Query("password") password: String): Call<UserResponse<Boolean>>
    //닉네임 수정
    @POST("mypage/profile/nickname")
    fun changeNickname(@Body nickname: String): Call<UserResponse<String>>
    //이메일 변경
    @POST("mypage/profile/email")
    fun changeEmail(@Query("userId") userId: Int, @Query("newEmail") email: String): Call<UserResponse<String>>
}