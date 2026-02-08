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

//카카오 연동 상태 확인
data class UsingKakao(
    @SerializedName(value = "kakaoEmail") var kakaoEmail: String?,
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

data class Tokens(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Int
)

//초대 코드 실시간 검증
data class InviteCodeValidate(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("targetUserNickname") val targetUserNickname: String
)

// 회원가입 시 이메일 인증 발송, 재발송
data class SignupLink(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)

// 이메일 인증 토큰 검사 여부
data class EmailVerificationStatus(
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("email") val email: String,
    @SerializedName("tokenStatus") val status: String
)

data class EmailSendRequest(
    val email : String
)
//이메일 변경 시 이메일 인증 발송, 재발송
data class EmailLink(
    @SerializedName("email") val email: String,
    @SerializedName("message") val message: String,
    @SerializedName("verificationToken") val token: String
)

data class KakaoLink(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("kakaoEmail") val kakaoEmail: String,
    @SerializedName("userInfo") val userInfo: KakaoUser
)
data class KakaoUser(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImg") val profileImg: String
)

// 이메일 중복 검사
data class EmailCheckDuplicated(
    @SerializedName("available") val available: Boolean,
    @SerializedName("message") val message: String
)

// 닉네임 중복 검사
data class NicknameCheckDuplicated(
    @SerializedName("available") val available: Boolean,
    @SerializedName("message") val message: String
)

data class SignupResult(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("birthDate") val birthDate: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("userInfo") val userInfo: UserInfo?
)

data class UserInfo(
    @SerializedName("nickname") val nickname: String,
    // TODO:: 도희님한테 프로필 이미지 추가 요청
    @SerializedName("profileImg") val profileImg: String?
)