package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.model.WeeklyStats
import com.artes.securehup.stepapp.domain.repository.StatsRepository
import javax.inject.Inject

/*
Burada kullanılan GetWeeklyStatsUseCase sınıfı, haftalık istatistiklerini getirir.
Home Screen için haftalık istatistiklerini getirmek için kullanılır.
Widget için güncel veri sağlamak için kullanılır.
Notification güncellemeleri için kullanılır.
*/
class GetWeeklyStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    suspend fun getCurrentWeekStats(): Result<WeeklyStats> {
        return statsRepository.getCurrentWeekStats()
    }
    
    suspend fun getRecentWeeksStats(limit: Int = 12): Result<List<WeeklyStats>> {
        return try {
            // Flow'u collect ederek list'e çeviriyoruz
            val flow = statsRepository.getRecentWeeklyStats(limit)
            // Bu kısım repository implementasyonunda düzeltilecek
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 