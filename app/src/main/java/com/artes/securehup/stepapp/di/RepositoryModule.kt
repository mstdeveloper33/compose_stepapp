package com.artes.securehup.stepapp.di

import com.artes.securehup.stepapp.data.repository.HealthRepositoryImpl
import com.artes.securehup.stepapp.data.repository.UserRepositoryImpl
import com.artes.securehup.stepapp.data.repository.StatsRepositoryImpl
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import com.artes.securehup.stepapp.domain.repository.UserRepository
import com.artes.securehup.stepapp.domain.repository.StatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        healthRepositoryImpl: HealthRepositoryImpl
    ): HealthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        statsRepositoryImpl: StatsRepositoryImpl
    ): StatsRepository
} 