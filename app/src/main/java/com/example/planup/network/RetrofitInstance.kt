package com.example.planup.network

import com.example.planup.main.goal.adapter.GoalApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://54.180.207.84:8080/"

    private val authHeaderAdder = Interceptor { chain ->
        val original = chain.request()

        if (original.header("Authorization") != null) {
            return@Interceptor chain.proceed(original)
        }

        val builder = original.newBuilder()

        val raw = App.jwt.token?.trim().orEmpty()
        if (raw.isNotEmpty()) {
            val value = if (raw.startsWith("Bearer ", ignoreCase = true)) raw else "Bearer $raw"
            builder.header("Authorization", value)
        }

        chain.proceed(builder.build())
    }

    private val httpLogging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authHeaderAdder)
            .addInterceptor(httpLogging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val termsApi: TermsApi by lazy { retrofit.create(TermsApi::class.java) }
    val profileApi: ProfileApi by lazy { retrofit.create(ProfileApi::class.java) }
    @Deprecated("Repository 에서 주입 받아 사용")
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val goalApi: GoalApi by lazy { retrofit.create(GoalApi::class.java) }
    val friendApi: FriendApi by lazy { retrofit.create(FriendApi::class.java) }
    val passwordApi: PasswordApi by lazy { retrofit.create(PasswordApi::class.java) }
    val weeklyReportApi: WeeklyReportApi by lazy { retrofit.create(WeeklyReportApi::class.java) }
    val verificationApi: VerificationApi by lazy { retrofit.create(VerificationApi::class.java) }
    val encourageMessageApi: EncourageMessageApi by lazy {
        getRetrofit().create(EncourageMessageApi::class.java)
    }
}
