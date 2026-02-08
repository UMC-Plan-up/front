package com.example.planup.signup.data

import com.google.gson.annotations.SerializedName

// 이메일 인증 대안 - 카카오 로그인
data class KakaoLoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("kakaoAccessToken") val kakaoAccessToken: String
)

// 카카오 회원가입 완료
// 요청 Dto
data class KakaoCompleteRequest(
    @SerializedName("tempUserId") val tempUserId: String,
    @SerializedName("name") val name: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("birthDate") val birthDate: String,
    @SerializedName("profileImg") val profileImg: String?,
    @SerializedName("agreements") val agreements: List<Agreement>
)