package com.example.planup.signup.data

import com.example.planup.network.SignupApi

class SignupRepository(
    private val api: SignupApi
) {
    suspend fun signup(signupRequestDto: SignupRequestDto) =
        api.signup(signupRequestDto)
}
