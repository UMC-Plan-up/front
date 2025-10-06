package com.example.planup.di

import android.content.Context
import com.example.planup.database.TokenSaver
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
}