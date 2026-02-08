package com.example.planup.main.user.domain

import com.example.planup.login.data.LoginResponse
import com.example.planup.main.user.data.UserInfoResponse
import com.example.planup.network.ApiResult
import com.example.planup.network.data.EmailLink
import com.example.planup.network.data.EmailVerificationStatus
import com.example.planup.network.data.Tokens
import com.example.planup.network.data.SignupLink
import com.example.planup.network.data.SignupResult
import com.example.planup.network.data.UsingKakao
import com.example.planup.network.data.WithDraw
import com.example.planup.signup.data.Agreement
import com.example.planup.signup.data.KakaoCompleteResponse
import com.example.planup.signup.data.KakaoLoginResponse
import com.example.planup.signup.data.ProcessResult
import com.example.planup.signup.data.ProfileImageResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.io.File

interface UserRepository {

    suspend fun kakaoLogin(
        kakaoAccessToken: String,
        email: String
    ): ApiResult<KakaoLoginResponse.ResultData>

    suspend fun completeKakaoLogin(
        tempUserId: String,
        name: String,
        nickname: String,
        gender: String,
        birthDate: String,
        profileImg: String?,
        agreements: List<Agreement>
    ): ApiResult<KakaoCompleteResponse.Result>

    /**
     * 내 초대 코드를 가져옵니다.
     */
    suspend fun getInviteCode(): String

    /**
     * 입력한 초대 코드를 검증하고,
     * 유효 하다면 초대코드를 활성화 합니다.
     */
    suspend fun validateInviteCode(
        code: String
    ): ApiResult<ProcessResult>

    /**
     * 이메일을 변경하기 위해 해당 이메일로 메일을 발송합니다.
     *
     * @param email 변경할 이메일 주소
     * @return
     */
    suspend fun sendMailForChange(
        email: String
    ) : ApiResult<EmailLink>

    /**
     * 이메일을 변경하기 위해 해당 이메일로 메일을 다시 발송합니다.
     *
     * @param email 변경할 이메일 주소
     * @return
     */
    suspend fun reSendMailForChange(
        email: String
    ) : ApiResult<EmailLink>


    suspend fun changeNickName(
        newNickName: String
    ): ApiResult<String>

    suspend fun postLogin(
        email: String,
        password: String
    ): ApiResult<LoginResponse.Result>

    suspend fun logout(): ApiResult<String>

    /**
     * 회원 탈퇴
     * @param reason 탈퇴 사유
     * @see WithDraw
     * @return
     */
    suspend fun withdraw(
        reason : String
    ) : ApiResult<WithDraw>

    suspend fun getUserInfo(): ApiResult<UserInfoResponse.Result>

    suspend fun setProfileImage(
        file: File
    ): ApiResult<ProfileImageResponse.Result>

    suspend fun setProfileImage(
        body: MultipartBody.Part
    ): ApiResult<ProfileImageResponse.Result>


    /**
     * 카카오 계정 연동 정보를 가져옵니다.
     *
     * @return
     */
    suspend fun getKakaoAccountLink(

    ) : ApiResult<UsingKakao>


    /**
     * 비밀번호 변경
     */
    suspend fun changePassword(
        newPassword: String
    ): ApiResult<Boolean>

    suspend fun getUserNickName() : String
    suspend fun getUserEmail() : String
    suspend fun getUserProfileImage() : String
    suspend fun getUserNotificationService(): Boolean
    suspend fun getUserNotificationMarketing(): Boolean

    suspend fun updateUserNotificationService(isOnNotification: Boolean): ApiResult<Boolean>
    suspend fun updateUserNotificationMarketing(isOnNotification: Boolean) : ApiResult<Boolean>

    /**
     * 이메일 중복을 여부를 확인합니다
     *
     * @param email
     * @return 사용 가능하면 true, 아니라면 false
     */
    suspend fun checkEmailAvailable(
        email: String
    ): ApiResult<Boolean>

    /**
     * 회원가입
     */
    suspend fun signup(
        email: String,
        password: String,
        passwordCheck: String,
        name: String,
        nickname: String,
        gender: String,
        birthDate: String,
        profileImg: String?,
        agreements: List<Agreement>
    ): ApiResult<SignupResult>


    /**
     * 회원가입을 하기 위해 해당 이메일로 메일을 발송합니다.
     *
     * @param email
     */
    suspend fun sendMailForSignup(
        email: String
    ): ApiResult<SignupLink>


    /**
     * 회원가입을 하기 위해 해당 이메일로 메일을 다시 발송합니다.
     *
     * @param email
     */
    suspend fun resendMailForSignup(
        email: String
    ): ApiResult<SignupLink>

    /**
     * 이메일 인증 토큰을 검사합니다
     *
     * @param verificationToken
     * @return
     */
    suspend fun checkEmailVerificationStatus(
        verificationToken: String
    ): ApiResult<EmailVerificationStatus>

    suspend fun checkNicknameDuplicated(
        nickname: String
    ): ApiResult<Boolean>

    suspend fun refreshToken(): ApiResult<Tokens>
}