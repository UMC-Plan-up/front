package com.example.planup.main.user.domain

import com.example.planup.login.data.LoginResponse
import com.example.planup.main.user.data.UserInfoResponse
import com.example.planup.network.ApiResult
import com.example.planup.network.data.ProfileImage
import com.example.planup.signup.data.InviteCodeValidateResponse
import okhttp3.MultipartBody
import java.io.File

interface UserRepository {

    /**
     * 내 초대 코드를 가져옵니다.
     */
    suspend fun getInviteCode(): String

    /**
     * 입력한 초대 코드를 검증합니다.
     */
    suspend fun validateInviteCode(
        code: String
    ): ApiResult<InviteCodeValidateResponse.Result>


    suspend fun changeNickName(
        newNickName: String
    ): ApiResult<String>

    suspend fun postLogin(
        email: String,
        password: String
    ): ApiResult<LoginResponse.Result>

    suspend fun getUserInfo(): ApiResult<UserInfoResponse.Result>

    suspend fun setProfileImage(
        file: File
    ): ApiResult<ProfileImage>

    suspend fun setProfileImage(
        body: MultipartBody.Part
    ): ApiResult<ProfileImage>


    suspend fun getUserEmail() : String
    suspend fun getUserProfileImage() : String

}