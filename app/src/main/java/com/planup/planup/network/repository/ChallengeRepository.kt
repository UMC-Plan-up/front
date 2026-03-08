package com.planup.planup.network.repository

import com.planup.planup.network.ApiResult
import com.planup.planup.network.data.ChallengeInfo

interface ChallengeRepository {
    suspend fun rejectChallenge(challengeId: Int, userId: Int): ApiResult<Boolean>
    suspend fun acceptChallenge(challengeId: Int, userId: Int): ApiResult<Boolean>
    suspend fun challengeInfo(challengeId: Int): ApiResult<ChallengeInfo>
}