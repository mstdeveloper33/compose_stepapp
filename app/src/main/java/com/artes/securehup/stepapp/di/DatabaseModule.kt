package com.artes.securehup.stepapp.di

import android.content.Context
import androidx.room.Room
import com.artes.securehup.stepapp.data.local.dao.HealthDataDao
import com.artes.securehup.stepapp.data.local.dao.UserProfileDao
import com.artes.securehup.stepapp.data.local.dao.WeeklyStatsDao
import com.artes.securehup.stepapp.data.local.database.HealthDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHealthDatabase(@ApplicationContext context: Context): HealthDatabase {
        return Room.databaseBuilder(
            context,
            HealthDatabase::class.java,
            HealthDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideHealthDataDao(database: HealthDatabase): HealthDataDao {
        return database.healthDataDao()
    }

    @Provides
    fun provideUserProfileDao(database: HealthDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideWeeklyStatsDao(database: HealthDatabase): WeeklyStatsDao {
        return database.weeklyStatsDao()
    }
} 