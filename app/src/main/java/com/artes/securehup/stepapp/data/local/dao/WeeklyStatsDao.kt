package com.artes.securehup.stepapp.data.local.dao

import androidx.room.*
import com.artes.securehup.stepapp.data.local.entity.WeeklyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyStatsDao {
    
    @Query("SELECT * FROM weekly_stats ORDER BY startDate DESC")
    fun getAllWeeklyStats(): Flow<List<WeeklyStatsEntity>>
    
    @Query("SELECT * FROM weekly_stats WHERE weekId = :weekId LIMIT 1")
    suspend fun getWeeklyStatsByWeekId(weekId: String): WeeklyStatsEntity?
    
    @Query("SELECT * FROM weekly_stats ORDER BY startDate DESC LIMIT :limit")
    fun getRecentWeeklyStats(limit: Int = 12): Flow<List<WeeklyStatsEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyStats(weeklyStats: WeeklyStatsEntity): Long
    
    @Update
    suspend fun updateWeeklyStats(weeklyStats: WeeklyStatsEntity)
    
    @Delete
    suspend fun deleteWeeklyStats(weeklyStats: WeeklyStatsEntity)
    
    @Query("DELETE FROM weekly_stats WHERE startDate < :cutoffDate")
    suspend fun deleteOldWeeklyStats(cutoffDate: java.util.Date)
} 