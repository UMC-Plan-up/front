package com.example.planup.network.data

import com.google.gson.annotations.SerializedName

//User Controller 기본 응답 양식, T는 String, Boolean 또는 아래 정의한 data class 중 하나
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
data class KakaoAccount(
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

//이메일 인증 발송, 재발송
data class EmailSend(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)

//프로필 사진 업로드 및 변경
data class ProfileImage(
    @SerializedName("imageUrl") val imageUrl: String
)