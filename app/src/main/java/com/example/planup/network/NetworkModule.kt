package com.example.planup.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://54.180.207.84:8080/"


//fun getRetrofit(): Retrofit {
//
//    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create()).build()
//
//    return retrofit
//}


//디버깅시 통신 로그를 보여주는 인터셉터
val logging = HttpLoggingInterceptor().apply{
    level=HttpLoggingInterceptor.Level.BODY
}

//JWT 토큰 관리를 위한 인터셉터
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req =
            chain.request().newBuilder().addHeader("Authorization", App.jwt.token ?: "").build()
        return chain.proceed(req)
    }
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .addInterceptor(logging)
    // 필요 시 타임아웃 등 다른 설정 추가
    .build()

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit
}