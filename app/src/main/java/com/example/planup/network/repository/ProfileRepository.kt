package com.example.planup.network.repository

import com.example.planup.network.ApiResult

interface ProfileRepository {
    suspend fun getRandomNickname(): ApiResult<String>
}