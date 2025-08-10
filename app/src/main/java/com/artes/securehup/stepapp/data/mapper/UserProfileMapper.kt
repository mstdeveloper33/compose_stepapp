package com.artes.securehup.stepapp.data.mapper

import com.artes.securehup.stepapp.data.local.entity.UserProfileEntity
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

/*
Burada kullanılan UserProfileMapper sınıfı, UserProfileEntity ve UserProfile arasında dönüşüm yapmak için kullanılır.
*/

@Singleton
class UserProfileMapper @Inject constructor() {
    
    // Entity'yi Domain'e çevirir. Burada entity'yi domain'e çevirirken, entity'deki verileri domain'deki verilerle eşleştirir. Neden? çevirme yapıyoruz sebebi ise eşleştirme yapıyoruz neden eşleştirme yapıyoruz? çünkü entity'deki verileri domain'deki verilerle eşleştirirken, entity'deki verileri domain'deki verilerle eşleştirir.
    fun entityToDomain(entity: UserProfileEntity): UserProfile {
        return UserProfile(
            id = entity.id,
            name = entity.name,
            age = entity.age,
            height = entity.height,
            weight = entity.weight,
            gender = when(entity.gender) {
                "MALE" -> Gender.MALE
                "FEMALE" -> Gender.FEMALE
                else -> Gender.OTHER
            },
            dailyStepGoal = entity.dailyStepGoal,
            dailyDistanceGoal = entity.dailyDistanceGoal,
            dailyCalorieGoal = entity.dailyCalorieGoal,
            dailyActiveTimeGoal = entity.dailyActiveTimeGoal,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    // Domain'i Entity'ye çevirir. Burada domain'i entity'ye çevirirken, domain'deki verileri entity'deki verilerle eşleştirir. Neden? çevirme yapıyoruz sebebi ise eşleştirme yapıyoruz neden eşleştirme yapıyoruz? çünkü domain'deki verileri entity'deki verilerle eşleştirirken, domain'deki verileri entity'deki verilerle eşleştirir.
    fun domainToEntity(domain: UserProfile): UserProfileEntity {
        return UserProfileEntity(
            id = domain.id,
            name = domain.name,
            age = domain.age,
            height = domain.height,
            weight = domain.weight,
            gender = when(domain.gender) {
                Gender.MALE -> "MALE"
                Gender.FEMALE -> "FEMALE"
                Gender.OTHER -> "OTHER"
            },
            dailyStepGoal = domain.dailyStepGoal,
            dailyDistanceGoal = domain.dailyDistanceGoal,
            dailyCalorieGoal = domain.dailyCalorieGoal,
            dailyActiveTimeGoal = domain.dailyActiveTimeGoal,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 