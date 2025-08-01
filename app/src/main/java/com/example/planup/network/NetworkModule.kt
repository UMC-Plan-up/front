package com.example.planup.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://http://54.180.207.84:8080"

fun getRetrofit(): Retrofit {

    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit
}

val logging = HttpLoggingInterceptor().apply{
    level=HttpLoggingInterceptor.Level.BODY
}

val builder = OkHttpClient.Builder()
    .addInterceptor(logging)
    // 필요 시 타임아웃 등 다른 설정 추가
    .build()

fun getRetrofit2(): Retrofit{

    val retrofit = Retrofit.Builder().client(builder)
        .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit
}