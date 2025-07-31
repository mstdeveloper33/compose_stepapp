package com.artes.securehup.stepapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1, // Tek kullanıcı profili
    val name: String,
    val age: Int,
    val height: Double, // cm cinsinden
    val weight: Double, // kg cinsinden
    val gender: String, // "MALE", "FEMALE", "OTHER"
    val dailyStepGoal: Int = 10000,
    val dailyDistanceGoal: Double = 7.0, // km cinsinden
    val dailyCalorieGoal: Int = 2000,
    val dailyActiveTimeGoal: Long = 60, // dakika
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 