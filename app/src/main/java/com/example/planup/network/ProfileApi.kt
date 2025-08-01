package com.example.planup.network

import com.example.planup.signup.data.ProfileImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi {
    @Multipart
    @POST("/profile/image")
    suspend fun uploadProfileImage(
        @Part file: MultipartBody.Part
    ): Response<ProfileImageResponse>
}
