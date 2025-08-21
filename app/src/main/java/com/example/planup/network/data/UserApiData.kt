package com.example.planup.network.data

import com.google.gson.annotations.SerializedName

//User Controller 기본 응답 양식
data class UserResponse<T>(
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: T
)

//내 초대코드 조회
data class MyInviteCode(
    @SerializedName("inviteCode") val inviteCode: String
)

//유저 정보 조회
data class UserInfo(
    @SerializedName(value = "id") var id: Int,
    @SerializedName(value = "email") var email: String,
    @SerializedName(value = "nickname") var nickname: String,
    @SerializedName(value = "profileImg") var profileImage: String
)

//카카오 연동 상태 확인
data class UsingKakao(
    @SerializedName(value = "kakaoEmail") var kakaoEmail: String,
    @SerializedName(value = "linked") var linked: Boolean
)

//회원 탈퇴
data class WithDraw(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("withdrawableDate") val data: String
)

//회원 가입
data class Signup(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("friendNickname") val nickname: String
)
//비밀번호 변경 시 링크 발송, 재발송
data class PasswordLink(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)
//로그인
data class Login(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String,
    @SerializedName("message") val message: String
)

//초대 코드 실시간 검증
data class InviteCodeValidate(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("targetUserNickname") val targetUserNickname: String
)

//회원가입 시 이메일 인증 발송, 재발송
data class SignupLink(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)

//이메일 변경 시 이메일 인증 발송, 재발송
data class EmailLink(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)

//프로필 사진 업로드 및 변경
data class ProfileImage(
    @SerializedName("file") val file: String
)

data class SyncKakao(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: KakaoAccount,
    @SerializedName("newUser") val newUser: Boolean
)
data class KakaoAccount(
    @SerializedName("tempUserId") val tempUserId: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("userInfo") val userInfo: UserInfo
)