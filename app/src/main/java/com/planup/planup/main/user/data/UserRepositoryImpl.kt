package com.planup.planup.main.user.data

import com.planup.planup.database.TokenSaver
import com.planup.planup.database.UserInfoSaver
import com.planup.planup.database.checkToken
import com.planup.planup.login.data.LoginRequest
import com.planup.planup.login.data.RefreshTokenRequest
import com.planup.planup.main.user.domain.UserNameAlreadyExistException
import com.planup.planup.main.user.domain.UserRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.UserApi
import com.planup.planup.network.data.EmailLink
import com.planup.planup.network.data.EmailSendRequest
import com.planup.planup.network.data.EmailVerificationStatus
import com.planup.planup.network.data.KakaoLogin
import com.planup.planup.network.data.KakaoSignup
import com.planup.planup.network.data.Login
import com.planup.planup.network.data.SignupLink
import com.planup.planup.network.data.SignupResult
import com.planup.planup.network.data.Tokens
import com.planup.planup.network.data.UserInfo
import com.planup.planup.network.data.UsingKakao
import com.planup.planup.network.data.WithDraw
import com.planup.planup.network.safeResult
import com.planup.planup.password.data.PasswordChangeRequest
import com.planup.planup.signup.data.Agreement
import com.planup.planup.signup.data.EmailSendRequestDto
import com.planup.planup.signup.data.InviteCodeRequest
import com.planup.planup.signup.data.InviteCodeValidateRequest
import com.planup.planup.signup.data.KakaoCompleteRequest
import com.planup.planup.signup.data.KakaoLoginRequest
import com.planup.planup.signup.data.ProcessResult
import com.planup.planup.signup.data.ProfileImageResponse
import com.planup.planup.signup.data.ResendEmailRequest
import com.planup.planup.signup.data.SignupRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenSaver: TokenSaver,
    private val userInfoSaver: UserInfoSaver
) : UserRepository {

    override suspend fun kakaoLogin(
        kakaoAccessToken: String,
        email: String
    ): ApiResult<KakaoLogin> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.kakaoLogin(KakaoLoginRequest(kakaoAccessToken = kakaoAccessToken, email = email))
            },
            onResponse = { response ->
                if(response.isSuccess) {
                    ApiResult.Success(response.result)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun completeKakaoLogin(
        tempUserId: String,
        name: String,
        nickname: String,
        gender: String,
        birthDate: String,
        profileImg: String?,
        agreements: List<Agreement>
    ): ApiResult<KakaoSignup> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.kakaoComplete(
                    KakaoCompleteRequest(
                        tempUserId = tempUserId,
                        name = name,
                        nickname = nickname,
                        gender = gender,
                        birthDate = birthDate,
                        profileImg = profileImg,
                        agreements = agreements
                    )
                )
            },
            onResponse = { response ->
                if(response.isSuccess) {
                    userInfoSaver.saveUserInfo(response.result.userInfo)
                    tokenSaver.saveToken(response.result.accessToken)
                    tokenSaver.saveRefreshToken(response.result.refreshToken)
                    ApiResult.Success(response.result)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun validateToken(): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                userApi.validateToken()
            },
            onResponse = { response ->
                if(response.isSuccess) {
                    ApiResult.Success(response.result)
                } else {
                    userInfoSaver.clearAllUserInfo()
                    tokenSaver.clearTokens()
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

    override suspend fun validateInviteCode(code: String): ApiResult<ProcessResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.validateInviteCode(InviteCodeValidateRequest(code))
                },
                onResponse = { inviteCodeValidateResponse ->
                    if (inviteCodeValidateResponse.isSuccess) {
                        if (inviteCodeValidateResponse.result.valid) {
                            val inviteCodeRequest = InviteCodeRequest(
                                code
                            )
                            safeResult(
                                response = {
                                    userApi.processInviteCode(inviteCodeRequest)
                                },
                                onResponse = { inviteCodeProcessResponse ->
                                    if (inviteCodeProcessResponse.isSuccess) {
                                        val result = inviteCodeProcessResponse.result
                                        if (result.success) {
                                            ApiResult.Success(result)
                                        } else {
                                            ApiResult.Fail(result.message)
                                        }
                                    } else {
                                        ApiResult.Fail(inviteCodeProcessResponse.message)
                                    }
                                }
                            )
                        } else {
                            ApiResult.Fail(inviteCodeValidateResponse.message)
                        }
                    } else {
                        ApiResult.Fail(inviteCodeValidateResponse.message)
                    }
                }
            )
        }

    override suspend fun sendMailForChange(email: String): ApiResult<EmailLink> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    val request = EmailSendRequest(email)
                    userApi.emailLink(request)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val emailSendResult = response.result
                        ApiResult.Success(emailSendResult)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun reSendMailForChange(email: String): ApiResult<EmailLink> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    val request = EmailSendRequest(email)
                    userApi
                    userApi.emailReLink(request)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val emailSendResult = response.result
                        ApiResult.Success(emailSendResult)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
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
    ): ApiResult<Login> = withContext(Dispatchers.IO) {
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
                        userInfoSaver.saveUserInfo(response.result.userInfo)

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
                    tokenSaver.clearTokens()
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
                        tokenSaver.clearTokens()
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

    override suspend fun getUserInfo(): ApiResult<UserInfo> =
        withContext(Dispatchers.IO) {
            if (userInfoSaver.isEmpty) {
                safeResult(
                    response = {
                        userApi.getUserInfo()
                    },
                    onResponse = { response ->
                        if (response.isSuccess) {
                            val result = response.result
                            userInfoSaver.saveUserInfo(result)

                            ApiResult.Success(result)
                        } else {
                            ApiResult.Fail(response.message)
                        }

                    }
                )
            } else {
                ApiResult.Success(
                    userInfoSaver.toUserInfo()
                )
            }
        }

    override suspend fun setProfileImage(file: File): ApiResult<ProfileImageResponse.Result> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        return setProfileImage(multipartBody)
    }

    override suspend fun setProfileImage(body: MultipartBody.Part): ApiResult<ProfileImageResponse.Result> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.setProfileImage(body)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        userInfoSaver.saveProfileImage(result.imageUrl)
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }


    override suspend fun getKakaoAccountLink(): ApiResult<UsingKakao> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = userApi::getKakaoAccountLink,
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun changePassword(
        newPassword: String
    ): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    val request = PasswordChangeRequest(
                        newPassword
                    )
                    userApi.changePassword(request)
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

    override suspend fun getUserNotificationService(): Boolean {
        return userInfoSaver.getNotificationService()
    }

    override suspend fun getUserNotificationMarketing(): Boolean {
        return userInfoSaver.getNotificationMarketing()
    }

    override suspend fun updateUserNotificationService(isOnNotification: Boolean) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.patchNoticeService()
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        userInfoSaver.saveNotificationService(result)
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun updateUserNotificationMarketing(isOnNotification: Boolean) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.patchNoticeMarketing()
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        userInfoSaver.saveNotificationMarketing(result)
                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun checkEmailAvailable(email: String): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.checkEmailDuplicate(email)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result.available)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun signup(
        email: String,
        password: String,
        passwordCheck: String,
        name: String,
        nickname: String,
        gender: String,
        birthDate: String,
        profileImg: String?,
        agreements: List<Agreement>
    ): ApiResult<SignupResult> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.signup(SignupRequestDto(
                            email = email,
                            password = password,
                            passwordCheck = passwordCheck,
                            name = name,
                            nickname = nickname,
                            gender = gender,
                            birthDate = birthDate,
                            profileImg = profileImg,
                            agreements = agreements
                        )
                    )
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result

                        tokenSaver.saveToken(result.accessToken)
                        tokenSaver.saveRefreshToken(result.refreshToken)

                        // 유저 정보도 바로 업데이트
                        userInfoSaver.clearAllUserInfo()
                        userInfoSaver.saveUserInfo(result.userInfo)

                        ApiResult.Success(result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun sendMailForSignup(email: String): ApiResult<SignupLink> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.sendEmail(EmailSendRequestDto(email))
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
        }

    override suspend fun resendMailForSignup(email: String): ApiResult<SignupLink> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.resendEmail(ResendEmailRequest(email))
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
        }

    override suspend fun checkEmailVerificationStatus(verificationToken: String): ApiResult<EmailVerificationStatus> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.checkEmailVerificationStatus(verificationToken)
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
        }

    override suspend fun checkNicknameDuplicated(nickname: String): ApiResult<Boolean> =
        withContext(Dispatchers.IO){
            safeResult(
                response = {
                    userApi.checkNicknameDuplicate(nickname)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result
                        ApiResult.Success(result.available)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
        }

    override suspend fun refreshToken(): ApiResult<Tokens> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    userApi.refreshToken(
                        RefreshTokenRequest(
                            tokenSaver.getRefreshToken() ?: ""
                        )
                    )
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        val result = response.result

                        tokenSaver.saveToken(result.accessToken)
                        tokenSaver.saveRefreshToken(result.refreshToken)

                        ApiResult.Success(result)
                    } else {
                        // 갱신 실패한 경우, 로그아웃 처리를 위한 정보 제거
                        userInfoSaver.clearAllUserInfo()
                        tokenSaver.clearTokens()

                        ApiResult.Fail(response.message)
                    }
                }
            )
        }
}