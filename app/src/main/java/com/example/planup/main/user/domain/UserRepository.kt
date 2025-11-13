package com.example.planup.main.user.domain

import com.example.planup.login.data.LoginResponse
import com.example.planup.main.user.data.UserInfoResponse
import com.example.planup.network.ApiResult
import com.example.planup.network.data.ProfileImage
import com.example.planup.network.data.WithDraw
import okhttp3.MultipartBody
import java.io.File

interface UserRepository {

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
    ): ApiResult<ProfileImage>

    suspend fun setProfileImage(
        body: MultipartBody.Part
    ): ApiResult<ProfileImage>


    suspend fun getUserNickName() : String
    suspend fun getUserEmail() : String
    suspend fun getUserProfileImage() : String
    suspend fun getUserNotificationLocal(): Boolean
    suspend fun getUserNotificationMarketing(): Boolean

    suspend fun updateUserNotificationLocal(isOnNotification: Boolean)
    suspend fun updateUserNotificationMarketing(isOnNotification: Boolean) : ApiResult<Boolean>

}