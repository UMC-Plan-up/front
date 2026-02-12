package com.planup.planup.main.goal.item

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoalRetrofitInstance {
    private const val BASE_URL = "http://54.180.207.84/"

    val api: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
