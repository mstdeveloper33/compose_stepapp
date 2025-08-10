package com.artes.securehup.stepapp.domain.model

import java.util.Date

/*
Burada kullanılan UserProfile sınıfı, kullanıcı profilini temsil eder.
Entity'den farkı şu : Entity'de verileri veritabanına kaydederken, Domain'de verileri kullanıcıya gösterirken kullanılır.
*/
data class UserProfile(
    val id: Long = 1,
    val name: String,
    val age: Int,
    val height: Double, // cm cinsinden
    val weight: Double, // kg cinsinden
    val gender: Gender,
    val dailyStepGoal: Int = 10000,
    val dailyDistanceGoal: Double = 7.0, // km cinsinden
    val dailyCalorieGoal: Int = 2000,
    val dailyActiveTimeGoal: Long = 60, // dakika
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    // Burada kullanılan getBMI fonksiyonu, kullanıcının vücut kitle indeksini hesaplar.
    fun getBMI(): Double {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }
    
    // Burada kullanılan getBMICategory fonksiyonu, kullanıcının vücut kitle indeksine göre vücut kitle indeksi kategorisini hesaplar.
    fun getBMICategory(): BMICategory {
        val bmi = getBMI()
        return when {
            bmi < 18.5 -> BMICategory.UNDERWEIGHT
            bmi < 25.0 -> BMICategory.NORMAL
            bmi < 30.0 -> BMICategory.OVERWEIGHT
            else -> BMICategory.OBESE
        }
    }
    
    // Burada kullanılan getBasalMetabolicRate fonksiyonu, kullanıcının bazal metabolizma hızını hesaplar.
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

/*
Burada kullanılan Gender enum sınıfı, kullanıcının cinsiyetini temsil eder.
*/
enum class Gender {
    MALE, FEMALE, OTHER
}

/*
Burada kullanılan BMICategory enum sınıfı, kullanıcının vücut kitle indeksi kategorisini temsil eder.
*/
enum class BMICategory {
    UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE
} 