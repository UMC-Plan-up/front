package com.planup.planup.network.repository

import com.planup.planup.network.ApiResult

interface ChallengeRepository {
    suspend fun rejectChallenge(challengeId: Int, userId: Int): ApiResult<Boolean>
    suspend fun acceptChallenge(challengeId: Int, userId: Int): ApiResult<Boolean>

}