package com.example.planup.di

import android.content.Context
import com.example.planup.database.TokenSaver
import com.example.planup.database.UserInfoSaver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideTokenSaver(
        @ApplicationContext context: Context
    ) : TokenSaver {
        return TokenSaver(context)
    }

    @Provides
    @Singleton
    fun provideUserInfoSaver(
        @ApplicationContext context: Context
    ) : UserInfoSaver {
        return UserInfoSaver(context)
    }
}