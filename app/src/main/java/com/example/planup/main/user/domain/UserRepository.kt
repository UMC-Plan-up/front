package com.example.planup.main.user.domain

import com.example.planup.network.ApiResult
import com.example.planup.signup.data.InviteCodeResult

interface UserRepository {

    suspend fun getInviteCode() : ApiResult<InviteCodeResult>
}