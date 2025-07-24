package com.artes.securehup.stepapp.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.artes.securehup.stepapp.data.local.dao.HealthDataDao
import com.artes.securehup.stepapp.data.local.dao.UserProfileDao
import com.artes.securehup.stepapp.data.local.dao.WeeklyStatsDao
import com.artes.securehup.stepapp.data.local.entity.HealthDataEntity
import com.artes.securehup.stepapp.data.local.entity.UserProfileEntity
import com.artes.securehup.stepapp.data.local.entity.WeeklyStatsEntity

@Database(
    entities = [
        HealthDataEntity::class,
        UserProfileEntity::class,
        WeeklyStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class HealthDatabase : RoomDatabase() {
    
    abstract fun healthDataDao(): HealthDataDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weeklyStatsDao(): WeeklyStatsDao
    
    companion object {
        const val DATABASE_NAME = "health_database"
    }
} 