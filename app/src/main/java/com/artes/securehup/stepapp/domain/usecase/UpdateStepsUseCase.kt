package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.repository.HealthRepository
import com.artes.securehup.stepapp.domain.repository.UserRepository
import com.artes.securehup.stepapp.domain.util.CalorieCalculator
import com.artes.securehup.stepapp.domain.util.ActivityType
import javax.inject.Inject

class UpdateStepsUseCase @Inject constructor(
    private val healthRepository: HealthRepository,
    private val userRepository: UserRepository,
    private val calorieCalculator: CalorieCalculator
) {
    suspend operator fun invoke(steps: Int): Result<Unit> {
        return try {
            // Adım sayısını güncelle
            healthRepository.updateStepsForToday(steps)
            
            // Kullanıcı profilini al ve kalori hesapla
            val userProfile = userRepository.getUserProfileSync()
            if (userProfile != null) {
                // Kalori hesaplama
                val calories = calorieCalculator.calculateCaloriesFromSteps(
                    steps = steps,
                    userProfile = userProfile,
                    activityType = ActivityType.WALKING_NORMAL
                )
                
                // Mesafe hesaplama
                val distance = calorieCalculator.calculateDistanceFromSteps(
                    steps = steps,
                    userProfile = userProfile
                )
                
                // Kalori ve mesafeyi güncelle
                healthRepository.updateCaloriesForToday(calories)
                healthRepository.updateDistanceForToday(distance)
                
                // Aktif süre hesaplama (yaklaşık - adım sayısından tahmin)
                val estimatedActiveTimeMinutes = (steps / 100).toLong() // 100 adım/dakika varsayımı
                healthRepository.updateActiveTimeForToday(estimatedActiveTimeMinutes)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 