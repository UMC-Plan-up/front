package com.example.planup.signup.data

import com.example.planup.network.UserApi
import retrofit2.Response

class SignupRepository(
    private val api: UserApi
) {
    suspend fun signup(signupRequestDto: SignupRequestDto): Response<SignupResponseDto> =
        api.signup(signupRequestDto)
}
