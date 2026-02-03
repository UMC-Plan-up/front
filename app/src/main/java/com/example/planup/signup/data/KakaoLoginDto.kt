package com.example.planup.signup.data

import com.google.gson.annotations.SerializedName

// 이메일 인증 대안 - 카카오 로그인
data class KakaoLoginRequest(
    @SerializedName("token") val token: String,
    @SerializedName("email") val email: String
)

data class KakaoLoginResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultData
) {
    data class ResultData(
        @SerializedName("tempUserId") val tempUserId: String,
        @SerializedName("accessToken") val accessToken: String?,
        @SerializedName("refreshToken") val refreshToken: String?,
        @SerializedName("userInfo") val userInfo: UserInfo?,
        @SerializedName("newUser") val newUser: Boolean
    )

    data class UserInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("email") val email: String?,
        @SerializedName("nickname") val nickname: String?,
        @SerializedName("profileImg") val profileImg: String?
    )
}

// 카카오 회원가입 완료
// 요청 Dto
data class KakaoCompleteRequest(
    @SerializedName("tempUserId") val tempUserId: String,
    @SerializedName("nickname") val nickname: String,
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
        @SerializedName("email") val email: String?,
        @SerializedName("accessToken") val accessToken: String,
        @SerializedName("refreshToken") val refreshToken: String,
        @SerializedName("userInfo") val userInfo: UserInfo?
    )

    data class UserInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("email") val email: String?,
        @SerializedName("nickname") val nickname: String?,
        @SerializedName("profileImg") val profileImg: String?
    )
}

