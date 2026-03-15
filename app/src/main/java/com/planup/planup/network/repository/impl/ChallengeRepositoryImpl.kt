package com.planup.planup.network.repository.impl

import com.planup.planup.network.ApiResult
import com.planup.planup.network.ChallengeApi
import com.planup.planup.network.data.ChallengeInfo
import com.planup.planup.network.repository.ChallengeRepository
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val challengeApi: ChallengeApi
): ChallengeRepository {
    override suspend fun rejectChallenge(
        challengeId: Int,
        userId: Int
    ): ApiResult<Boolean> = withContext(Dispatchers.IO){
        safeResult(
            response = {
                challengeApi.rejectChallenge(challengeId, userId)
            },
            onResponse = { response ->
                if (response.isSuccess) {
                    ApiResult.Success(true)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }



    override suspend fun acceptChallenge(
        challengeId: Int,
        userId: Int
    ): ApiResult<Boolean> = withContext(Dispatchers.IO){
        safeResult(
            response = {
                challengeApi.acceptChallenge(challengeId, userId)
            },
            onResponse = { response ->
                if (response.isSuccess) {
                    ApiResult.Success(true)
                } else {
                    ApiResult.Fail(response.message)
                }
            }
        )
    }

    override suspend fun challengeInfo(challengeId: Int): ApiResult<ChallengeInfo>
        = withContext(Dispatchers.IO){
            safeResult(
                response = {
                    challengeApi.challengeInfo(challengeId)
                },
                onResponse = { response ->
                    if (response.isSuccess) {
                        ApiResult.Success(response.result)
                    } else {
                        ApiResult.Fail(response.message)
                    }
                }
            )
    }

}