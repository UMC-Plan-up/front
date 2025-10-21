package com.example.planup.main.user.data

import com.example.planup.database.TokenSaver
import com.example.planup.database.UserInfoSaver
import com.example.planup.database.checkToken
import com.example.planup.main.user.domain.UserNameAlreadyExistException
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.UserApi
import com.example.planup.network.safeResult
import com.example.planup.signup.data.InviteCodeResult
import com.example.planup.signup.data.InviteCodeValidateRequest
import com.example.planup.signup.data.InviteCodeValidateResponse
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


    override suspend fun changeNickName(newNickName: String) = withContext(Dispatchers.IO) {
        val prevName = userInfoSaver.getNickName()
        if (prevName == newNickName){
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
}