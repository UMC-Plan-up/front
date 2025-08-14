package com.example.planup.signup.data

// 카카오 로그인
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
data class KakaoCompleteRequest(
    val tempUserId: String,
    val nickname: String,
    val profileImg: String?,
    val agreements: List<Agreement>,
    val inviteCode: String?
) {
    data class Agreement(
        val termsId: Int,
        val isAgreed: Boolean
    )
}

data class KakaoCompleteResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val id: Long,
        val email: String?,
        val friendNickname: String?,
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
