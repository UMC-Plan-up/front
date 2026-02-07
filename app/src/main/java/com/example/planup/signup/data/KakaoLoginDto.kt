package com.example.planup.signup.data

import com.example.planup.network.data.UserInfo
import com.google.gson.annotations.SerializedName

// 이메일 인증 대안 - 카카오 로그인
data class KakaoLoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("kakaoAccessToken") val kakaoAccessToken: String
)

data class KakaoLoginResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultData
) {
    data class ResultData(
        @SerializedName("userStatus") val userStatus: UserStatus,
        @SerializedName("tempUserId") val tempUserId: String?,
        @SerializedName("accessToken") val accessToken: String?,
        @SerializedName("refreshToken") val refreshToken: String?,
        @SerializedName("userInfo") val userInfo: UserInfo?,
    )

    data class UserInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("email") val email: String?,
        @SerializedName("nickname") val nickname: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("birthDate") val birthDate: String?,
        @SerializedName("profileImg") val profileImg: String?,
        @SerializedName("ServiceNotificationAllow") val serviceNotificationAllow: Boolean,
        @SerializedName("marketingNotificationAllow") val marketingNotificationAllow: Boolean,
    )}

enum class UserStatus {
    // 일반 로그인 성공 또는 기존 카카오 유저의 로그인 성공
    LOGIN_SUCCESS,
    // 일반 회원가입 완료 또는 카카오 임시 유저의 추가 정보 입력 완료
    SIGNUP_SUCCESS,
    // 카카오 인증은 마쳤으나 추가 정보 입력이 필요한 신규 유저
    SIGNUP_REQUIRED,
    // 카카오 로그인 시도 시, 동일한 이메일로 가입된 일반 계정이 이미 존재할 때
    ACCOUNT_CONFLICT
}

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

// 응답 Dto
data class KakaoCompleteResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result
) {
    data class Result(
        @SerializedName("id") val id: Long,
        @SerializedName("email") val email: String,
        @SerializedName("name") val name: String,
        @SerializedName("birthDate") val birthDate: String,
        @SerializedName("accessToken") val accessToken: String,
        @SerializedName("refreshToken") val refreshToken: String,
        @SerializedName("userInfo") val userInfo: UserInfo?
    )
}

