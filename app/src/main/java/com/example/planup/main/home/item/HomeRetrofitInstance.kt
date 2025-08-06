package com.example.planup.main.home.item

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HomeRetrofitInstance {
    private const val BASE_URL = "http://54.180.207.84:8080"

    val api: GoalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoalApiService::class.java)
    }
}