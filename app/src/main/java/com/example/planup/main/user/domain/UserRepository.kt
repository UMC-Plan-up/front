package com.example.planup.main.user.domain

import com.example.planup.network.ApiResult
import com.example.planup.signup.data.InviteCodeResult
import com.example.planup.signup.data.InviteCodeValidateResponse

interface UserRepository {

    /**
     * 내 초대 코드를 가져옵니다.
     */
    suspend fun getInviteCode(): String

    /**
     * 입력한 초대 코드를 검증합니다.
     */
    suspend fun validateInviteCode(
        code: String
    ): ApiResult<InviteCodeValidateResponse.Result>


    suspend fun changeNickName(
        newNickName: String
    ): ApiResult<String>
}