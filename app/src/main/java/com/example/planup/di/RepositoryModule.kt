package com.example.planup.di

import com.example.planup.main.friend.data.FriendRepositoryImpl
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.user.data.UserRepositoryImpl
import com.example.planup.main.user.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Hilt를 통해 FriendRepositoryImpl을 FriendRepository로 바인딩합니다.
     */
    @Binds
    abstract fun bindFriendRepository(
        impl: FriendRepositoryImpl
    ): FriendRepository

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

}