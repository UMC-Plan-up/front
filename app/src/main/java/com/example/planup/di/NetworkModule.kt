package com.example.planup.di

import com.example.planup.database.TokenSaver
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.FriendApi
import com.example.planup.network.GoalApi
import com.example.planup.network.ProfileApi
import com.example.planup.network.TermsApi
import com.example.planup.network.UserApi
import com.example.planup.network.interceptor.AuthInterceptor
import com.example.planup.network.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        private const val BASE_URL = "http://54.180.207.84/"
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenSaver: TokenSaver
    ): AuthInterceptor {
        return AuthInterceptor(tokenSaver)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        userRepository: dagger.Lazy<UserRepository>
    ): TokenAuthenticator {
        return TokenAuthenticator(userRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .authenticator(authenticator)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFriendApi(
        retrofit: Retrofit
    ): FriendApi {
        return retrofit.create(FriendApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        retrofit: Retrofit
    ): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGoalApi(
        retrofit: Retrofit
    ): GoalApi {
        return retrofit.create(GoalApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTermApi(
        retrofit: Retrofit
    ): TermsApi {
        return retrofit.create(TermsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(
        retrofit: Retrofit
    ): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }
}