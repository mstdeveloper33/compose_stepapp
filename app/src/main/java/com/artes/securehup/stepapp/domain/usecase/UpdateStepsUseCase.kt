package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.repository.HealthRepository
import com.artes.securehup.stepapp.domain.repository.UserRepository
import com.artes.securehup.stepapp.domain.util.CalorieCalculator
import com.artes.securehup.stepapp.domain.util.ActivityType
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/*
Burada kullanılan UpdateStepsUseCase sınıfı, adım verilerini günceller.
Device'in bugünkü toplam adımını günceller.
Kullanıcı profilini alır ve kalori hesaplar.
Delta adım ve süre ile hesaplar.
Delta kaloriyi hesaplar ve bugünkü toplam kaloriye ekle.
Delta mesafe (aktivite tipine göre adım boyu) hesaplar ve bugünkü toplam mesafeye ekle.
Delta aktif süre (dakika) hesaplar ve bugünkü toplam aktif süreye ekle.
*/

class UpdateStepsUseCase @Inject constructor(
    private val healthRepository: HealthRepository,
    private val userRepository: UserRepository,
    private val calorieCalculator: CalorieCalculator
) {
    private companion object {
        // "Hareket halinde" saymak için minimum kadans (adım/dakika)
        private const val MIN_ACTIVE_CADENCE_SPM = 40.0
    }

    /**
     * Hassas güncelleme: delta adım ve süre ile hesaplar
     * @param totalSteps Device'in bugünkü toplam adımı
     * @param deltaSteps Son çağrıdan bu yana eklenen adım
     * @param deltaMillis Son çağrıdan bu yana geçen süre (ms)
     */
    suspend operator fun invoke(totalSteps: Int, deltaSteps: Int, deltaMillis: Long): Result<Unit> {
        return try {
            // Adım sayısını güncelle (toplam)
            healthRepository.updateStepsForToday(totalSteps)
            
            // Kullanıcı profilini al ve kalori hesapla
            val userProfile = userRepository.getUserProfileSync()
            if (userProfile != null) {
                // Kadans (adım/dakika) hesapla -> aktivite tipini seç
                val cadenceSpm = if (deltaMillis > 0) deltaSteps * 60_000.0 / deltaMillis else 0.0
                val activityType = when {
                    cadenceSpm < 80 -> ActivityType.WALKING_SLOW
                    cadenceSpm < 110 -> ActivityType.WALKING_NORMAL
                    cadenceSpm < 130 -> ActivityType.WALKING_FAST
                    cadenceSpm < 150 -> ActivityType.RUNNING_SLOW
                    cadenceSpm < 180 -> ActivityType.RUNNING_NORMAL
                    else -> ActivityType.RUNNING_FAST
                }

                // Hareket kontrolü: boşta kalınan süreyi filtrele
                val isMoving = deltaSteps > 0 || cadenceSpm >= MIN_ACTIVE_CADENCE_SPM

                // Önce bugünkü mevcut değerleri oku
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                val todayData = healthRepository.getHealthDataByDate(today)

                if (isMoving && deltaMillis > 0) {
                    // Delta kaloriyi hesapla ve bugünkü toplam kaloriye ekle
                    val deltaMinutes = deltaMillis / 60000.0
                    val deltaCalories = calorieCalculator.calculateCaloriesFromActivity(
                        durationMinutes = deltaMinutes,
                        userProfile = userProfile,
                        activityType = activityType
                    )
                    val newCaloriesTotal = (todayData?.calories ?: 0) + deltaCalories
                    healthRepository.updateCaloriesForToday(newCaloriesTotal)

                    // Delta mesafe (aktivite tipine göre adım boyu)
                    val deltaDistance = calorieCalculator.calculateDistanceFromSteps(
                        steps = deltaSteps,
                        userProfile = userProfile,
                        activityType = activityType
                    )
                    val newDistanceTotal = (todayData?.distance ?: 0.0) + deltaDistance
                    healthRepository.updateDistanceForToday(newDistanceTotal)

                    // Delta aktif süre (dakika)
                    val deltaActiveMinutes = (deltaMillis / 60000.0).toLong()
                    val newActiveMinutesTotal = (todayData?.activeTime ?: 0L) + deltaActiveMinutes
                    healthRepository.updateActiveTimeForToday(newActiveMinutesTotal)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 