package com.artes.securehup.stepapp.domain.util

import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class CalorieCalculator @Inject constructor() {
    
    companion object {
        // MET (Metabolic Equivalent of Task) değerleri
        private const val WALKING_SLOW_MET = 2.5    // Yavaş yürüme (3 km/h)
        private const val WALKING_NORMAL_MET = 3.3  // Normal yürüme (4-5 km/h)
        private const val WALKING_FAST_MET = 4.3    // Hızlı yürüme (6 km/h)
        private const val RUNNING_SLOW_MET = 6.0    // Yavaş koşma (7 km/h)
        private const val RUNNING_NORMAL_MET = 8.3  // Normal koşma (8-9 km/h)
        private const val RUNNING_FAST_MET = 11.5   // Hızlı koşma (10+ km/h)
        
        // Ortalama adım uzunlukları (metre)
        private const val AVERAGE_STEP_LENGTH_MALE = 0.78
        private const val AVERAGE_STEP_LENGTH_FEMALE = 0.70
        
        // Ortalama adım/dakika değerleri farklı aktiviteler için
        private const val STEPS_PER_MINUTE_WALKING = 100
        private const val STEPS_PER_MINUTE_FAST_WALKING = 120
        private const val STEPS_PER_MINUTE_RUNNING = 160
    }
    
    /**
     * Adım sayısına göre kalori hesaplama
     */
    fun calculateCaloriesFromSteps(
        steps: Int,
        userProfile: UserProfile,
        activityType: ActivityType = ActivityType.WALKING_NORMAL
    ): Int {
        if (steps <= 0) return 0
        
        val durationMinutes = estimateActivityDuration(steps, activityType)
        return calculateCaloriesFromActivity(
            durationMinutes = durationMinutes,
            userProfile = userProfile,
            activityType = activityType
        )
    }
    
    /**
     * Aktivite süresi ve tipine göre kalori hesaplama
     * Kalori = MET × Ağırlık (kg) × Süre (saat)
     */
    fun calculateCaloriesFromActivity(
        durationMinutes: Double,
        userProfile: UserProfile,
        activityType: ActivityType
    ): Int {
        if (durationMinutes <= 0) return 0
        
        val durationHours = durationMinutes / 60.0
        val met = activityType.metValue
        val weightKg = userProfile.weight
        
        val calories = met * weightKg * durationHours
        return calories.roundToInt()
    }
    
    /**
     * Adım sayısından mesafe hesaplama (kilometre)
     */
    fun calculateDistanceFromSteps(steps: Int, userProfile: UserProfile): Double {
        if (steps <= 0) return 0.0
        
        val stepLength = when (userProfile.gender) {
            Gender.MALE -> AVERAGE_STEP_LENGTH_MALE
            Gender.FEMALE -> AVERAGE_STEP_LENGTH_FEMALE
            Gender.OTHER -> (AVERAGE_STEP_LENGTH_MALE + AVERAGE_STEP_LENGTH_FEMALE) / 2
        }
        
        // Boy faktörü ekleyelim (standart 170cm'e göre ayarlama)
        val heightFactor = userProfile.height / 170.0
        val adjustedStepLength = stepLength * heightFactor
        
        val distanceMeters = steps * adjustedStepLength
        return distanceMeters / 1000.0 // Kilometre'ye çevir
    }
    
    /**
     * Adım sayısından aktivite süresini tahmin etme
     */
    private fun estimateActivityDuration(steps: Int, activityType: ActivityType): Double {
        val stepsPerMinute = when (activityType) {
            ActivityType.WALKING_SLOW -> STEPS_PER_MINUTE_WALKING * 0.8
            ActivityType.WALKING_NORMAL -> STEPS_PER_MINUTE_WALKING.toDouble()
            ActivityType.WALKING_FAST -> STEPS_PER_MINUTE_FAST_WALKING.toDouble()
            ActivityType.RUNNING_SLOW -> STEPS_PER_MINUTE_RUNNING * 0.8
            ActivityType.RUNNING_NORMAL -> STEPS_PER_MINUTE_RUNNING.toDouble()
            ActivityType.RUNNING_FAST -> STEPS_PER_MINUTE_RUNNING * 1.2
        }
        
        return steps / stepsPerMinute
    }
    
    /**
     * Adım hızından aktivite tipini tahmin etme (gelişmiş özellik)
     */
    fun estimateActivityType(
        stepCount: Int,
        timeWindowMinutes: Double
    ): ActivityType {
        if (timeWindowMinutes <= 0) return ActivityType.WALKING_NORMAL
        
        val stepsPerMinute = stepCount / timeWindowMinutes
        
        return when {
            stepsPerMinute < 80 -> ActivityType.WALKING_SLOW
            stepsPerMinute < 110 -> ActivityType.WALKING_NORMAL
            stepsPerMinute < 130 -> ActivityType.WALKING_FAST
            stepsPerMinute < 150 -> ActivityType.RUNNING_SLOW
            stepsPerMinute < 180 -> ActivityType.RUNNING_NORMAL
            else -> ActivityType.RUNNING_FAST
        }
    }
    
    /**
     * Günlük hedef kalori önerisi
     */
    fun calculateDailyCalorieGoal(
        userProfile: UserProfile,
        activityLevel: ActivityLevel = ActivityLevel.MODERATE
    ): Int {
        val bmr = userProfile.getBasalMetabolicRate()
        val activityMultiplier = activityLevel.multiplier
        
        val totalDailyEnergyExpenditure = bmr * activityMultiplier
        
        // Egzersizden gelen ek kalori hedefi (BMR'nin %15-25'i)
        val exerciseCalorieGoal = (bmr * 0.20).roundToInt()
        
        return exerciseCalorieGoal
    }
}

enum class ActivityType(val metValue: Double) {
    WALKING_SLOW(2.5),
    WALKING_NORMAL(3.3),
    WALKING_FAST(4.3),
    RUNNING_SLOW(6.0),
    RUNNING_NORMAL(8.3),
    RUNNING_FAST(11.5)
}

enum class ActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),    // Masa başı iş, egzersiz yok
    LIGHT(1.375),      // Hafif egzersiz/spor 1-3 gün/hafta
    MODERATE(1.55),    // Orta düzey egzersiz/spor 3-5 gün/hafta
    ACTIVE(1.725),     // Yoğun egzersiz/spor 6-7 gün/hafta
    VERY_ACTIVE(1.9)   // Çok yoğun egzersiz/fiziksel iş
} 