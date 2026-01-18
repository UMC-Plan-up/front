package com.example.planup.network.repository.impl

import com.example.planup.network.ApiResult
import com.example.planup.network.ProfileApi
import com.example.planup.network.repository.ProfileRepository
import com.example.planup.network.safeResult
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