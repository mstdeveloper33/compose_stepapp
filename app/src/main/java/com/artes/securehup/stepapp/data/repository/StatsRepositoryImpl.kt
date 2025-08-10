package com.artes.securehup.stepapp.data.repository

import com.artes.securehup.stepapp.data.local.dao.WeeklyStatsDao
import com.artes.securehup.stepapp.data.mapper.WeeklyStatsMapper
import com.artes.securehup.stepapp.domain.model.WeeklyStats
import com.artes.securehup.stepapp.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/*
Burada kullanılan StatsRepositoryImpl sınıfı, StatsRepository arayüzünü uygular.
StatsRepository arayüzünü uygularken, WeeklyStatsDao ve WeeklyStatsMapper sınıflarını kullanır.
WeeklyStatsDao sınıfı, haftalık istatistiklerini veritabanına kaydeder, getirir ve günceller.
WeeklyStatsMapper sınıfı, WeeklyStatsEntity ve WeeklyStats arasında dönüşüm yapmak için kullanılır.

UI/ViewModel 
    ↓ (WeeklyStats istedi)
StatsRepository Interface 
    ↓ (Implementation'a yönlendi)
StatsRepositoryImpl 
    ↓ (DAO'yu çağırdı)
WeeklyStatsDao 
    ↓ (Entity döndü)
WeeklyStatsMapper 
    ↓ (Domain'e çevirdi)
WeeklyStats → UI'a döndü 
*/
@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val weeklyStatsDao: WeeklyStatsDao,
    private val mapper: WeeklyStatsMapper
) : StatsRepository {

    override fun getAllWeeklyStats(): Flow<List<WeeklyStats>> {
        return weeklyStatsDao.getAllWeeklyStats().map { entities ->
            entities.map { mapper.entityToDomain(it) }
        }
    }

    override suspend fun getWeeklyStatsByWeekId(weekId: String): WeeklyStats? {
        val entity = weeklyStatsDao.getWeeklyStatsByWeekId(weekId)
        return entity?.let { mapper.entityToDomain(it) }
    }

    override fun getRecentWeeklyStats(limit: Int): Flow<List<WeeklyStats>> {
        return weeklyStatsDao.getRecentWeeklyStats(limit).map { entities ->
            entities.map { mapper.entityToDomain(it) }
        }
    }

    override suspend fun insertWeeklyStats(weeklyStats: WeeklyStats): Long {
        val entity = mapper.domainToEntity(weeklyStats)
        return weeklyStatsDao.insertWeeklyStats(entity)
    }

    override suspend fun updateWeeklyStats(weeklyStats: WeeklyStats) {
        val entity = mapper.domainToEntity(weeklyStats)
        weeklyStatsDao.updateWeeklyStats(entity)
    }

    override suspend fun deleteWeeklyStats(weeklyStats: WeeklyStats) {
        val entity = mapper.domainToEntity(weeklyStats)
        weeklyStatsDao.deleteWeeklyStats(entity)
    }

    override suspend fun deleteOldWeeklyStats(cutoffDate: Date) {
        weeklyStatsDao.deleteOldWeeklyStats(cutoffDate)
    }

    override suspend fun generateWeeklyStats(startDate: Date, endDate: Date): Result<WeeklyStats> {
        return try {
            // Bu fonksiyon gerçek health data'dan haftalık istatistik üretecek
            // Şu an basit bir örnek döndürüyoruz
            val weekId = generateWeekId(startDate)
            val stats = WeeklyStats(
                weekId = weekId,
                startDate = startDate,
                endDate = endDate,
                totalSteps = 0,
                totalDistance = 0.0,
                totalCalories = 0,
                totalActiveTime = 0L,
                averageSteps = 0,
                averageCalories = 0,
                averageActiveTime = 0L
            )
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentWeekStats(): Result<WeeklyStats> {
        return try {
            val calendar = Calendar.getInstance()
            val weekId = generateWeekId(calendar.time)
            
            val existingStats = getWeeklyStatsByWeekId(weekId)
            if (existingStats != null) {
                Result.success(existingStats)
            } else {
                // Generate new stats for current week
                val startOfWeek = getStartOfWeek(calendar.time)
                val endOfWeek = getEndOfWeek(calendar.time)
                generateWeeklyStats(startOfWeek, endOfWeek)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeekStatsForDate(date: Date): Result<WeeklyStats> {
        return try {
            val weekId = generateWeekId(date)
            val stats = getWeeklyStatsByWeekId(weekId)
            
            if (stats != null) {
                Result.success(stats)
            } else {
                val startOfWeek = getStartOfWeek(date)
                val endOfWeek = getEndOfWeek(date)
                generateWeeklyStats(startOfWeek, endOfWeek)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateWeekId(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        return "$year-W${week.toString().padStart(2, '0')}"
    }

    private fun getStartOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getEndOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = getStartOfWeek(date)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }
} 