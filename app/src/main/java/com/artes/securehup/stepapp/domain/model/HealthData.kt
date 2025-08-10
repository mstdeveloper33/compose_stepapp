package com.artes.securehup.stepapp.domain.model

import java.util.Date

/*
Burada kullanılan HealthData sınıfı, adım verilerini temsil eder.
Entity'den farkı şu : Entity'de verileri veritabanına kaydederken, Domain'de verileri kullanıcıya gösterirken kullanılır.

*/
data class HealthData(
    val id: Long = 0,
    val date: Date,
    val steps: Int = 0,
    val distance: Double = 0.0, // kilometre cinsinden
    val calories: Int = 0,
    val activeTime: Long = 0L, // dakika cinsinden
    val createdAt: Date = Date()
) {
    // Burada kullanılan getStepProgress fonksiyonu, adım hedefine göre adım ilerlemeyi hesaplar.
    fun getStepProgress(goalSteps: Int): Float {
        return if (goalSteps > 0) (steps.toFloat() / goalSteps.toFloat()).coerceAtMost(1.0f) else 0f
    }
    
    // Burada kullanılan getCalorieProgress fonksiyonu, kalori hedefine göre kalori ilerlemeyi hesaplar.
    fun getCalorieProgress(goalCalories: Int): Float {
        return if (goalCalories > 0) (calories.toFloat() / goalCalories.toFloat()).coerceAtMost(1.0f) else 0f
    }
    
    // Burada kullanılan getActiveTimeProgress fonksiyonu, aktif zaman hedefine göre aktif zaman ilerlemeyi hesaplar.
    fun getActiveTimeProgress(goalActiveTime: Long): Float {
        return if (goalActiveTime > 0) (activeTime.toFloat() / goalActiveTime.toFloat()).coerceAtMost(1.0f) else 0f
    }
    
    // Burada kullanılan getDistanceInKm fonksiyonu, mesafe kilometre cinsinden gösterir.
    fun getDistanceInKm(): String {
        return String.format("%.2f", distance)
    }
    
    // Burada kullanılan getActiveTimeInHours fonksiyonu, aktif zaman saat cinsinden gösterir.
    fun getActiveTimeInHours(): String {
        val hours = activeTime / 60
        val minutes = activeTime % 60
        return if (hours > 0) "${hours}s ${minutes}dk" else "${minutes}dk"
    }
} 