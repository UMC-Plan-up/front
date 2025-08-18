package com.example.planup.signup.data

// 이메일 인증 대안 - 카카오 로그인
data class KakaoLoginRequest(
    val code: String
)

data class KakaoLoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
) {
    data class ResultData(
        val tempUserId: String?,
        val accessToken: String?,
        val userInfo: UserInfo?,
        val newUser: Boolean
    )

    data class UserInfo(
        val id: Long,
        val email: String?,
        val nickname: String?,
        val profileImg: String?
    )
}

// 카카오 회원가입 완료
// 요청 Dto
data class KakaoCompleteRequest(
    val tempUserId: String,
    val nickname: String,
    val profileImg: String?,
    val agreements: List<Agreement>
) {
    data class Agreement(
        val termsId: Int,
        val isAgreed: Boolean
    )
}

// 응답 Dto
data class KakaoCompleteResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val id: Long,
        val email: String?,
        val accessToken: String,
        val userInfo: UserInfo?
    )

    data class UserInfo(
        val id: Long,
        val email: String?,
        val nickname: String?,
        val profileImg: String?
    )
}

