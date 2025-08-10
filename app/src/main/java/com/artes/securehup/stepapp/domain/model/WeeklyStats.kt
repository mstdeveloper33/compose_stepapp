package com.artes.securehup.stepapp.domain.model

import android.annotation.SuppressLint
import java.util.Date

/*
Burada kullanılan WeeklyStats sınıfı, haftalık istatistiklerini temsil eder.
Entity'den farkı şu : Entity'de verileri veritabanına kaydederken, Domain'de verileri kullanıcıya gösterirken kullanılır.
*/
data class WeeklyStats(
    val weekId: String, // "2024-W01" format
    val startDate: Date,
    val endDate: Date,
    val totalSteps: Int,
    val totalDistance: Double,
    val totalCalories: Int,
    val totalActiveTime: Long,
    val averageSteps: Int,
    val averageCalories: Int,
    val averageActiveTime: Long,
    val createdAt: Date = Date()
) {
    // Burada kullanılan getFormattedWeek fonksiyonu, hafta numarasını formatlar.
    fun getFormattedWeek(): String {
        val weekNumber = weekId.substringAfter("W")
        val year = weekId.substringBefore("-W")
        return "$weekNumber. Hafta $year"
    }
    
    // Burada kullanılan getTotalDistanceFormatted fonksiyonu, toplam mesafeyi formatlar.
    @SuppressLint("DefaultLocale")
    fun getTotalDistanceFormatted(): String {
        return String.format("%.2f km", totalDistance)
    }
    
    // Burada kullanılan getTotalActiveTimeFormatted fonksiyonu, toplam aktif zamanı formatlar.
    fun getTotalActiveTimeFormatted(): String {
        val hours = totalActiveTime / 60
        val minutes = totalActiveTime % 60
        return if (hours > 0) "${hours}s ${minutes}dk" else "${minutes}dk"
    }
    
    // Burada kullanılan getAverageActiveTimeFormatted fonksiyonu, ortalama aktif zamanı formatlar.
    fun getAverageActiveTimeFormatted(): String {
        val hours = averageActiveTime / 60
        val minutes = averageActiveTime % 60
        return if (hours > 0) "${hours}s ${minutes}dk" else "${minutes}dk"
    }
    
    // Burada kullanılan getStepGoalAchievementRate fonksiyonu, adım hedefine göre adım ilerlemeyi hesaplar.
    fun getStepGoalAchievementRate(dailyStepGoal: Int): Float {
        return if (dailyStepGoal > 0) {
            (averageSteps.toFloat() / dailyStepGoal.toFloat()).coerceAtMost(1.0f)
        } else 0f
    }
} 