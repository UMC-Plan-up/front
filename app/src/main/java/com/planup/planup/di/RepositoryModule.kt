package com.planup.planup.di

import com.planup.planup.main.friend.data.FriendRepositoryImpl
import com.planup.planup.main.friend.domain.FriendRepository
import com.planup.planup.main.goal.data.GoalRepositoryImpl
import com.planup.planup.main.goal.domain.GoalRepository
import com.planup.planup.main.user.data.UserRepositoryImpl
import com.planup.planup.main.user.domain.UserRepository
import com.planup.planup.network.repository.ProfileRepository
import com.planup.planup.network.repository.TermRepository
import com.planup.planup.network.repository.impl.ProfileRepositoryImpl
import com.planup.planup.network.repository.impl.TermRepositoryImpl
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

    @Binds
    abstract fun bindGoalRepository(
        impl: GoalRepositoryImpl
    ): GoalRepository
  
    @Binds
    abstract fun bindTermRepository(
        impl: TermRepositoryImpl
    ): TermRepository

    @Binds
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

}