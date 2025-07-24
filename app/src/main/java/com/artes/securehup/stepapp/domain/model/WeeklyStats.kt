package com.artes.securehup.stepapp.domain.model

import android.annotation.SuppressLint
import java.util.Date

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
    fun getFormattedWeek(): String {
        val weekNumber = weekId.substringAfter("W")
        val year = weekId.substringBefore("-W")
        return "$weekNumber. Hafta $year"
    }
    
    @SuppressLint("DefaultLocale")
    fun getTotalDistanceFormatted(): String {
        return String.format("%.2f km", totalDistance)
    }
    
    fun getTotalActiveTimeFormatted(): String {
        val hours = totalActiveTime / 60
        val minutes = totalActiveTime % 60
        return if (hours > 0) "${hours}s ${minutes}dk" else "${minutes}dk"
    }
    
    fun getAverageActiveTimeFormatted(): String {
        val hours = averageActiveTime / 60
        val minutes = averageActiveTime % 60
        return if (hours > 0) "${hours}s ${minutes}dk" else "${minutes}dk"
    }
    
    fun getStepGoalAchievementRate(dailyStepGoal: Int): Float {
        return if (dailyStepGoal > 0) {
            (averageSteps.toFloat() / dailyStepGoal.toFloat()).coerceAtMost(1.0f)
        } else 0f
    }
} 