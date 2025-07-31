package com.example.planup.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://54.180.207.84:8080"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val signupApi: SignupApi by lazy {
        retrofit.create(SignupApi::class.java)
    }

    val termsApi: TermsApi by lazy {
        retrofit.create(TermsApi::class.java)
    }
}

