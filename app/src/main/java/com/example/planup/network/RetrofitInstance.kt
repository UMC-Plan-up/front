package com.example.planup.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://54.180.207.84:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val termsApi: TermsApi by lazy {
        retrofit.create(TermsApi::class.java)
    }

    val profileApi: ProfileApi by lazy {
        retrofit.create(ProfileApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val goalApi: GoalApi by lazy {
        retrofit.create(GoalApi::class.java)
    }

    val friendApi: FriendApi by lazy{
        retrofit.create(FriendApi::class.java)
    }

    val passwordApi: PasswordApi by lazy {
        retrofit.create(PasswordApi::class.java)
    }

    val weeklyReportApi: WeeklyReportApi by lazy{
        retrofit.create(WeeklyReportApi::class.java)
    }

    val goalReportApi: GoalReportApi by lazy{
        retrofit.create(GoalReportApi::class.java)
    }

    val verificationApi: VerificationApi by lazy{
        retrofit.create(VerificationApi::class.java)
    }

}
