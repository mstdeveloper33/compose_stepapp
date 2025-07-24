package com.artes.securehup.stepapp.domain.repository

import com.artes.securehup.stepapp.domain.model.WeeklyStats
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface StatsRepository {
    
    fun getAllWeeklyStats(): Flow<List<WeeklyStats>>
    
    suspend fun getWeeklyStatsByWeekId(weekId: String): WeeklyStats?
    
    fun getRecentWeeklyStats(limit: Int = 12): Flow<List<WeeklyStats>>
    
    suspend fun insertWeeklyStats(weeklyStats: WeeklyStats): Long
    
    suspend fun updateWeeklyStats(weeklyStats: WeeklyStats)
    
    suspend fun deleteWeeklyStats(weeklyStats: WeeklyStats)
    
    suspend fun deleteOldWeeklyStats(cutoffDate: Date)
    
    suspend fun generateWeeklyStats(startDate: Date, endDate: Date): Result<WeeklyStats>
    
    suspend fun getCurrentWeekStats(): Result<WeeklyStats>
    
    suspend fun getWeekStatsForDate(date: Date): Result<WeeklyStats>
} 