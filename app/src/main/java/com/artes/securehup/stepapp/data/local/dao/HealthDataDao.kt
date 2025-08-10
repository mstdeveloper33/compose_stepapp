package com.artes.securehup.stepapp.data.local.dao

import androidx.room.*
import com.artes.securehup.stepapp.data.local.entity.HealthDataEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HealthDataDao {
    //Tüm verileri tarih sırasına göre sırala
    @Query("SELECT * FROM health_data ORDER BY date DESC")
    fun getAllHealthData(): Flow<List<HealthDataEntity>>
    
    //Belirtilen tarihteki verileri getir
    @Query("SELECT * FROM health_data WHERE date = :date LIMIT 1")
    suspend fun getHealthDataByDate(date: Date): HealthDataEntity?
    
    //Belirtilen tarih aralığındaki verileri getir
    @Query("SELECT * FROM health_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getHealthDataBetweenDates(startDate: Date, endDate: Date): Flow<List<HealthDataEntity>>
    
    //Belirtilen tarih aralığındaki verileri getir
    @Query("SELECT * FROM health_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getHealthDataBetweenDatesSync(startDate: Date, endDate: Date): List<HealthDataEntity>
    
    //Belirtilen tarihten sonraki verileri getir
    @Query("SELECT * FROM health_data WHERE date >= :date ORDER BY date DESC LIMIT :limit")
    fun getRecentHealthData(date: Date, limit: Int = 7): Flow<List<HealthDataEntity>>
    
    //Belirtilen tarih aralığındaki toplam adım sayısını getir
    @Query("SELECT SUM(steps) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Int?
    
    //Belirtilen tarih aralığındaki toplam kalori sayısını getir
    @Query("SELECT SUM(calories) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Int?
    
    //Belirtilen tarih aralığındaki toplam mesafeyi getir
    @Query("SELECT SUM(distance) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalDistanceBetweenDates(startDate: Date, endDate: Date): Double?
    
    //Belirtilen tarih aralığındaki toplam aktif zamanı getir
    @Query("SELECT SUM(activeTime) FROM health_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalActiveTimeBetweenDates(startDate: Date, endDate: Date): Long?
    
    //Veri ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthData(healthData: HealthDataEntity): Long
    
    //Veri güncelle
    @Update
    suspend fun updateHealthData(healthData: HealthDataEntity)
    
    //Veri sil
    @Delete
    suspend fun deleteHealthData(healthData: HealthDataEntity)
    
    //Belirtilen tarihten önceki verileri sil
    @Query("DELETE FROM health_data WHERE date < :cutoffDate")
    suspend fun deleteOldData(cutoffDate: Date)
} 

/*
Burada kullanılan HealthDataDao sınıfı, Room veritabanında adım verilerini yönetir.
Adım verilerini getirir, eklemek, güncellemek ve silmek için kullanılır.
Ayrıca buradaki dao yapıları repository katmanına refaranstır yani repository katmanında kullanılır.
*/