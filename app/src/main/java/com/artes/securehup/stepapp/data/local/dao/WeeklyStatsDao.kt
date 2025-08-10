package com.artes.securehup.stepapp.data.local.dao

import androidx.room.*
import com.artes.securehup.stepapp.data.local.entity.WeeklyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyStatsDao {

    //Tüm haftalık istatistikleri tarih sırasına göre sırala
    @Query("SELECT * FROM weekly_stats ORDER BY startDate DESC")
    fun getAllWeeklyStats(): Flow<List<WeeklyStatsEntity>>
    
    //Belirtilen hafta için haftalık istatistikleri getir
    @Query("SELECT * FROM weekly_stats WHERE weekId = :weekId LIMIT 1")
    suspend fun getWeeklyStatsByWeekId(weekId: String): WeeklyStatsEntity?
    
    //Son 12 haftalık istatistikleri getir
    @Query("SELECT * FROM weekly_stats ORDER BY startDate DESC LIMIT :limit")
    fun getRecentWeeklyStats(limit: Int = 12): Flow<List<WeeklyStatsEntity>>
    
    //Haftalık istatistikleri ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyStats(weeklyStats: WeeklyStatsEntity): Long
    
    //Haftalık istatistikleri güncelle
    @Update
    suspend fun updateWeeklyStats(weeklyStats: WeeklyStatsEntity)
    
    //Haftalık istatistikleri sil
    @Delete
    suspend fun deleteWeeklyStats(weeklyStats: WeeklyStatsEntity)
    
    //Belirtilen tarihten önceki haftalık istatistikleri sil
    @Query("DELETE FROM weekly_stats WHERE startDate < :cutoffDate")
    suspend fun deleteOldWeeklyStats(cutoffDate: java.util.Date)
} 

/*
Burada kullanılan WeeklyStatsDao sınıfı, Room veritabanında haftalık istatistiklerini yönetir.
Haftalık istatistiklerini getirir, eklemek, güncellemek ve silmek için kullanılır.
Haftalık istatistiklerini getirirken, haftalık istatistikleri tarih sırasına göre sıralanır.
Haftalık istatistiklerini eklemek için, haftalık istatistikleri WeeklyStatsEntity sınıfında belirtilir.
Haftalık istatistiklerini güncellemek için, haftalık istatistikleri WeeklyStatsEntity sınıfında belirtilir.
Haftalık istatistiklerini silmek için, haftalık istatistikleri WeeklyStatsEntity sınıfında belirtilir.
Belirtilen tarihten önceki haftalık istatistikleri silmek için, belirtilen tarihi cutoffDate parametresi olarak alır ve bu tarihten önceki haftalık istatistikleri siler.
Burada kullanılan HealthDataDao sınıfı, Room veritabanında adım verilerini yönetir.
Adım verilerini getirir, eklemek, güncellemek ve silmek için kullanılır.
Ayrıca buradaki dao yapıları repository katmanına refaranstır yani repository katmanında kullanılır.
*/