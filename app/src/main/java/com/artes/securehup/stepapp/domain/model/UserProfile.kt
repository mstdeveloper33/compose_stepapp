package com.artes.securehup.stepapp.domain.model

import java.util.Date

data class UserProfile(
    val id: Long = 1,
    val name: String,
    val age: Int,
    val height: Double, // cm cinsinden
    val weight: Double, // kg cinsinden
    val gender: Gender,
    val dailyStepGoal: Int = 10000,
    val dailyCalorieGoal: Int = 2000,
    val dailyActiveTimeGoal: Long = 60, // dakika
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun getBMI(): Double {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }
    
    fun getBMICategory(): BMICategory {
        val bmi = getBMI()
        return when {
            bmi < 18.5 -> BMICategory.UNDERWEIGHT
            bmi < 25.0 -> BMICategory.NORMAL
            bmi < 30.0 -> BMICategory.OVERWEIGHT
            else -> BMICategory.OBESE
        }
    }
    
    fun getBasalMetabolicRate(): Int {
        // Harris-Benedict Equation
        return when (gender) {
            Gender.MALE -> (88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)).toInt()
            Gender.FEMALE -> (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)).toInt()
            Gender.OTHER -> ((88.362 + 447.593) / 2 + (13.397 + 9.247) / 2 * weight + 
                           (4.799 + 3.098) / 2 * height - (5.677 + 4.330) / 2 * age).toInt()
        }
    }
}

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class BMICategory {
    UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE
} 