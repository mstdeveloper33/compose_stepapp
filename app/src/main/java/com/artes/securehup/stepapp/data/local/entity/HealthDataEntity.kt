package com.artes.securehup.stepapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "health_data")
data class HealthDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val steps: Int = 0,
    val distance: Double = 0.0, // kilometre cinsinden
    val calories: Int = 0,
    val activeTime: Long = 0L, // dakika cinsinden
    val createdAt: Date = Date()
) 