package com.example.planup.network

import com.example.planup.signup.data.ProfileImageResponse
import com.example.planup.signup.data.RandomNicknameResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi {
    @Multipart
    @POST("/profile/image")
    suspend fun uploadProfileImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ProfileImageResponse>

    // 랜덤 닉네임 생성
    @GET("/profile/nickname/random")
    suspend fun getRandomNickname(
    ): Response<RandomNicknameResponse>
}
