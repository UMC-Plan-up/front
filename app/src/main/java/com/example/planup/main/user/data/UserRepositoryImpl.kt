package com.example.planup.main.user.data

import com.example.planup.database.TokenSaver
import com.example.planup.database.UserInfoSaver
import com.example.planup.database.checkToken
import com.example.planup.login.data.LoginRequest
import com.example.planup.login.data.LoginResponse
import com.example.planup.main.user.domain.UserNameAlreadyExistException
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.UserApi
import com.example.planup.network.data.ProfileImage
import com.example.planup.network.data.UserResponse
import com.example.planup.network.data.UsingKakao
import com.example.planup.network.data.WithDraw
import com.example.planup.network.safeResult
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenSaver: TokenSaver,
    private val userInfoSaver: UserInfoSaver
) : UserRepository {

    /**
     * [safeResult]의 UserResponse<T>응답 처리 형태 개선 버전
     *
     * @param response API 콜을 통한 Response<UserResponse<T>> 응답
     * @param onResponse 응답 성공시 T를 기반으로 ApiResult<R> 결과값 반환
     */
    private suspend inline fun <T, R> safeUserResponseResult(
        response: suspend () -> Response<UserResponse<T>>,
        onResponse: (T) -> ApiResult<R>
    ): ApiResult<R> {
        return safeResult(
            response = response,
            onResponse = { response ->
                if (response.isSuccess) {
                    val result = response.result
                    onResponse(result)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

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


    override suspend fun changeNickName(newNickName: String) = withContext(Dispatchers.IO) {
        val prevName = userInfoSaver.getNickName()
        if (prevName == newNickName) {
            return@withContext ApiResult.Exception(
                error = UserNameAlreadyExistException()
            )
        }
        tokenSaver.checkToken { token ->
            safeResult(
                response = {
                    userApi.changeNickname(newNickName)
                },
                onResponse = { userResponse ->
                    if (userResponse.isSuccess) {
                        val nickName = userResponse.result
                        userInfoSaver.saveNickName(nickName)
                        ApiResult.Success(nickName)
                    } else {
                        ApiResult.Fail(userResponse.message)
                    }
                }
            )
        }
    }

    override suspend fun postLogin(
        email: String,
        password: String
    ): ApiResult<LoginResponse.Result> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.login(LoginRequest(email = email, password = password))
            },
            onResponse = { response ->
                if (response.isSuccess) {
                    val result = response.result

                    // 토큰 여부 검증
                    if (result.accessToken.isNotEmpty()) {
                        // 토큰에 Bearer 붙이지 않고 저장
                        tokenSaver.saveToken(result.accessToken)

                        userInfoSaver.clearAllUserInfo()
                        userInfoSaver.saveNickName(result.nickname)
                        userInfoSaver.saveEmail(email)
                        userInfoSaver.saveProfileImage(result.profileImgUrl)
                    }

                    ApiResult.Success(result)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun logout(): ApiResult<String> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.logout()
            },
            onResponse = { response ->
                if (response.isSuccess) {
                    val result = response.result
                    userInfoSaver.clearAllUserInfo()
                    ApiResult.Success(result)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun withdraw(
        reason: String
    ): ApiResult<WithDraw> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.withdrawAccount(reason)
            },
            onResponse = { response ->
                if (response.isSuccess) {
                    val withdraw = response.result
                    if (withdraw.success) {
                        userInfoSaver.clearAllUserInfo()
                        ApiResult.Success(withdraw)
                    } else {
                        ApiResult.Fail(withdraw.message)
                    }
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun getUserInfo(): ApiResult<UserInfoResponse.Result> =
        withContext(Dispatchers.IO) {
            if (userInfoSaver.isEmpty) {
                safeResult(
                    response = {
                        userApi.getUserInfo()
                    },
                    onResponse = { response ->
                        if (response.isSuccess) {
                            val result = response.result

                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }

                    }
                )
            } else {
                // TODO:: UserId 가 필요하지 않으면 id 에는 더미 값 제공
                ApiResult.Success(
                    UserInfoResponse.Result(
                        id = -1,
                        email = userInfoSaver.getEmail(),
                        nickname = userInfoSaver.getNickName(),
                        profileImage = userInfoSaver.getProfileImage() ?: ""
                    )
                )
            }
        }

    override suspend fun setProfileImage(file: File): ApiResult<ProfileImage> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        return setProfileImage(multipartBody)
    }

    override suspend fun setProfileImage(body: MultipartBody.Part): ApiResult<ProfileImage> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.setProfileImage(body)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        when (response.code) {
                            "200" -> {
                                userInfoSaver.saveProfileImage(result.file)
                                ApiResult.Success(result)
                            }

                            else -> ApiResult.Fail(response.message)
                        }
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun getKakaoAccountLink(): ApiResult<UsingKakao> =
        withContext(Dispatchers.IO) {
            safeUserResponseResult(
                response = userApi::getKakaoAccountLink
            )  { usingKakao ->
                ApiResult.Success(usingKakao)
            }
        }

    override suspend fun getUserNickName(): String {
        return userInfoSaver.getNickName()
    }

    override suspend fun getUserEmail(): String {
        return userInfoSaver.getEmail()
    }

    override suspend fun getUserProfileImage(): String {
        return userInfoSaver.getProfileImage()
    }

    override suspend fun getUserNotificationLocal(): Boolean {
        return userInfoSaver.getNotificationLocal()
    }

    override suspend fun updateUserNotificationLocal(isOnNotification: Boolean) {
        userInfoSaver.saveNotificationLocal(isOnNotification)
    }
}