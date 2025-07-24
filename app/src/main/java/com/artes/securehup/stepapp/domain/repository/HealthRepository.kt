package com.artes.securehup.stepapp.domain.repository

import com.artes.securehup.stepapp.domain.model.HealthData
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HealthRepository {
    
    // Health Data Operations
    fun getAllHealthData(): Flow<List<HealthData>>
    
    suspend fun getHealthDataByDate(date: Date): HealthData?
    
    fun getHealthDataBetweenDates(startDate: Date, endDate: Date): Flow<List<HealthData>>
    
    fun getRecentHealthData(date: Date, limit: Int = 7): Flow<List<HealthData>>
    
    suspend fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Int
    
    suspend fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Int
    
    suspend fun getTotalDistanceBetweenDates(startDate: Date, endDate: Date): Double
    
    suspend fun getTotalActiveTimeBetweenDates(startDate: Date, endDate: Date): Long
    
    suspend fun insertHealthData(healthData: HealthData): Long
    
    suspend fun updateHealthData(healthData: HealthData)
    
    suspend fun deleteHealthData(healthData: HealthData)
    
    suspend fun deleteOldData(cutoffDate: Date)
    
    // Daily Sync Operations
    suspend fun syncTodaysData(): Result<HealthData>
    
    suspend fun updateStepsForToday(steps: Int): Result<Unit>
    
    suspend fun updateCaloriesForToday(calories: Int): Result<Unit>
    
    suspend fun updateActiveTimeForToday(activeTime: Long): Result<Unit>
    
    suspend fun updateDistanceForToday(distance: Double): Result<Unit>
} 