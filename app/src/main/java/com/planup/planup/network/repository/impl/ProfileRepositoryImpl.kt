package com.planup.planup.network.repository.impl

import com.planup.planup.network.ApiResult
import com.planup.planup.network.ProfileApi
import com.planup.planup.network.repository.ProfileRepository
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRepository {

    override suspend fun getRandomNickname(): ApiResult<String> = withContext(Dispatchers.IO) {
        safeResult(
            response = {
                profileApi.getRandomNickname()
            },
            onResponse = { response ->
                if (response.isSuccess) {
                    ApiResult.Success(response.result.nickname)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }
}