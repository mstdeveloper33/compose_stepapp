package com.artes.securehup.stepapp.domain.repository

import com.artes.securehup.stepapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface UserRepository {
    
    fun getUserProfile(): Flow<UserProfile?>
    
    suspend fun getUserProfileSync(): UserProfile?
    
    suspend fun insertUserProfile(userProfile: UserProfile): Long
    
    suspend fun updateUserProfile(userProfile: UserProfile)
    
    suspend fun updateStepGoal(stepGoal: Int)
    
    suspend fun updateDistanceGoal(distanceGoal: Double)
    
    suspend fun updateCalorieGoal(calorieGoal: Int)
    
    suspend fun updateActiveTimeGoal(activeTimeGoal: Long)
    
    suspend fun updateWeight(weight: Double, updatedAt: Date = Date())
    
    suspend fun isProfileCompleted(): Boolean
    
    suspend fun createDefaultProfile(name: String): Result<UserProfile>
} 