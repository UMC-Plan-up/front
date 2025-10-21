package com.example.planup.main.user.data

import com.example.planup.database.TokenSaver
import com.example.planup.database.UserInfoSaver
import com.example.planup.database.checkToken
import com.example.planup.login.data.LoginRequest
import com.example.planup.login.data.LoginResponse
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.UserApi
import com.example.planup.network.safeResult
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenSaver: TokenSaver,
    private val userInfoSaver: UserInfoSaver
) : UserRepository {

    override suspend fun getInviteCode(): String {
        val inviteCode = userInfoSaver.getInviteCode()
        if (inviteCode.isNullOrEmpty()) {
            val result = fetchInviteCode()
            if (result is ApiResult.Success) {
                userInfoSaver.saveInviteCode(result.data.inviteCode)
            }
            return userInfoSaver.getInviteCode() ?: ""
        } else {
            return inviteCode
        }
    }

    private suspend fun fetchInviteCode() = withContext(Dispatchers.IO) {
        tokenSaver.checkToken { token ->
            safeResult(
                response = {
                    userApi.getInviteCode(token)
                },
                onResponse = { inviteCodeResponse ->
                    if (inviteCodeResponse.isSuccess) {
                        val result = inviteCodeResponse.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(inviteCodeResponse.message)
                    }
                }
            )
        }
    }

    override suspend fun validateInviteCode(code: String): ApiResult<InviteCodeValidateResponse.Result> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        userApi.validateInviteCode(InviteCodeValidateRequest(code))
                    },
                    onResponse = { inviteCodeValidateResponse ->
                        if (inviteCodeValidateResponse.isSuccess) {
                            val result = inviteCodeValidateResponse.result
                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(inviteCodeValidateResponse.message)
                        }
                    }
                )
            }
        }

    override suspend fun postLogin(email: String, password: String): ApiResult<LoginResponse.Result> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.login(LoginRequest(email = email, password = password))
            },
            onResponse =  { response ->
                if(response.isSuccess) {
                    val result = response.result

                    // 토큰 여부 검증
                    if(result.accessToken.isNotEmpty()) {
                        // 토큰에 Bearer 붙이지 않고 저장
                        tokenSaver.saveToken(result.accessToken)

                        userInfoSaver.clearAllUserInfo()
                        userInfoSaver.saveNickName(result.nickname)
                        userInfoSaver.saveEmail(email)
                        userInfoSaver.saveProfileImg(result.profileImgUrl)
                    }

                    ApiResult.Success(result)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun getUserInfo(): ApiResult<UserInfoResponse.Result> = withContext(Dispatchers.IO) {
       if(userInfoSaver.isEmpty) {
            safeResult(
                response = {
                    userApi.getUserInfo()
                },
                onResponse = { response ->
                    if(response.isSuccess) {
                        val result = response.result

                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }

                }
            )
        } else {
            // TODO:: UserId 가 필요하지 않으면 id 에는 더미 값 제공
            ApiResult.Success(UserInfoResponse.Result(
                id = -1,
                email = userInfoSaver.getEmail(),
                nickname = userInfoSaver.getNickName(),
                profileImage = userInfoSaver.getProfileImg() ?: ""
            ))
        }
    }

}