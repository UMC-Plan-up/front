package com.example.planup.signup.data


import com.example.planup.network.SignupApi
import retrofit2.Response

class SignupRepository(
    private val api: SignupApi
) {
    suspend fun signup(signupRequestDto: SignupRequestDto): Response<SignupResponseDto> =
        api.signup(signupRequestDto)
}