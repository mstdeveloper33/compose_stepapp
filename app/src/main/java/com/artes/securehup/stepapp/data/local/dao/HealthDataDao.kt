package com.artes.securehup.stepapp.data.local.dao

import androidx.room.*
import com.artes.securehup.stepapp.data.local.entity.HealthDataEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HealthDataDao {
    
    @Query("SELECT * FROM health_data ORDER BY date DESC")
    fun getAllHealthData(): Flow<List<HealthDataEntity>>
    
    @Query("SELECT * FROM health_data WHERE date = :date LIMIT 1")
    suspend fun getHealthDataByDate(date: Date): HealthDataEntity?
    
    @Query("SELECT * FROM health_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getHealthDataBetweenDates(startDate: Date, endDate: Date): Flow<List<HealthDataEntity>>
    
    @Query("SELECT * FROM health_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getHealthDataBetweenDatesSync(startDate: Date, endDate: Date): List<HealthDataEntity>
    
    @Query("SELECT * FROM health_data WHERE date >= :date ORDER BY date DESC LIMIT :limit")
    fun getRecentHealthData(date: Date, limit: Int = 7): Flow<List<HealthDataEntity>>
    
    @Query("SELECT SUM(steps) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Int?
    
    @Query("SELECT SUM(calories) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Int?
    
    @Query("SELECT SUM(distance) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalDistanceBetweenDates(startDate: Date, endDate: Date): Double?
    
    @Query("SELECT SUM(activeTime) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalActiveTimeBetweenDates(startDate: Date, endDate: Date): Long?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthData(healthData: HealthDataEntity): Long
    
    @Update
    suspend fun updateHealthData(healthData: HealthDataEntity)
    
    @Delete
    suspend fun deleteHealthData(healthData: HealthDataEntity)
    
    @Query("DELETE FROM health_data WHERE date < :cutoffDate")
    suspend fun deleteOldData(cutoffDate: Date)
} 