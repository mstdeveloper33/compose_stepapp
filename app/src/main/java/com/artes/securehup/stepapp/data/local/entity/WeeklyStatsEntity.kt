package com.artes.securehup.stepapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weekly_stats")
data class WeeklyStatsEntity(
    @PrimaryKey
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
) 