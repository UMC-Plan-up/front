package com.planup.planup.network.repository

import com.planup.planup.network.ApiResult

interface ProfileRepository {
    suspend fun getRandomNickname(): ApiResult<String>
}