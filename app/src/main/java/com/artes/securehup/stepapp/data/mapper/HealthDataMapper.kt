package com.artes.securehup.stepapp.data.mapper

import com.artes.securehup.stepapp.data.local.entity.HealthDataEntity
import com.artes.securehup.stepapp.data.local.entity.WeeklyStatsEntity
import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.model.WeeklyStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthDataMapper @Inject constructor() {
    
    fun entityToDomain(entity: HealthDataEntity): HealthData {
        return HealthData(
            id = entity.id,
            date = entity.date,
            steps = entity.steps,
            distance = entity.distance,
            calories = entity.calories,
            activeTime = entity.activeTime,
            createdAt = entity.createdAt
        )
    }
    
    fun domainToEntity(domain: HealthData): HealthDataEntity {
        return HealthDataEntity(
            id = domain.id,
            date = domain.date,
            steps = domain.steps,
            distance = domain.distance,
            calories = domain.calories,
            activeTime = domain.activeTime,
            createdAt = domain.createdAt
        )
    }
}



@Singleton
class WeeklyStatsMapper @Inject constructor() {
    
    fun entityToDomain(entity: WeeklyStatsEntity): WeeklyStats {
        return WeeklyStats(
            weekId = entity.weekId,
            startDate = entity.startDate,
            endDate = entity.endDate,
            totalSteps = entity.totalSteps,
            totalDistance = entity.totalDistance,
            totalCalories = entity.totalCalories,
            totalActiveTime = entity.totalActiveTime,
            averageSteps = entity.averageSteps,
            averageCalories = entity.averageCalories,
            averageActiveTime = entity.averageActiveTime,
            createdAt = entity.createdAt
        )
    }
    
    fun domainToEntity(domain: WeeklyStats): WeeklyStatsEntity {
        return WeeklyStatsEntity(
            weekId = domain.weekId,
            startDate = domain.startDate,
            endDate = domain.endDate,
            totalSteps = domain.totalSteps,
            totalDistance = domain.totalDistance,
            totalCalories = domain.totalCalories,
            totalActiveTime = domain.totalActiveTime,
            averageSteps = domain.averageSteps,
            averageCalories = domain.averageCalories,
            averageActiveTime = domain.averageActiveTime,
            createdAt = domain.createdAt
        )
    }
} 