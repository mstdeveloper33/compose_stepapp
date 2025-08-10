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
    version = 3,
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

/*
Burada kullanılan HealthDatabase sınıfı, Room veritabanını oluşturur ve veritabanına erişim sağlar.
Veritabanına erişim sağlamak için, HealthDataDao, UserProfileDao ve WeeklyStatsDao sınıflarını kullanır.
Veritabanının adını DATABASE_NAME sabitinde belirtilir.
Veritabanının sürüm numarasını version sabitinde belirtilir.
Veritabanının şemasını exportSchema sabitinde belirtilir.
Veritabanının şemasını TypeConverters sınıfında belirtilir.
*/