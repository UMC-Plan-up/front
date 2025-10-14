package com.example.planup.main.user.data

import com.example.planup.database.TokenSaver
import com.example.planup.database.checkToken
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.UserApi
import com.example.planup.network.safeResult
import com.example.planup.signup.data.InviteCodeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenSaver: TokenSaver
) : UserRepository {

    override suspend fun getInviteCode(): ApiResult<InviteCodeResult> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        userApi.getInviteCode(token)
                    },
                    onResponse = {inviteCodeResponse ->
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

}