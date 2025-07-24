package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getUserProfile(): UserProfile? {
        return userRepository.getUserProfileSync()
    }
    
    fun getUserProfileFlow(): Flow<UserProfile?> {
        return userRepository.getUserProfile()
    }
    
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            userRepository.updateUserProfile(userProfile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateGoals(
        stepGoal: Int? = null,
        calorieGoal: Int? = null,
        activeTimeGoal: Long? = null
    ): Result<Unit> {
        return try {
            stepGoal?.let { userRepository.updateStepGoal(it) }
            calorieGoal?.let { userRepository.updateCalorieGoal(it) }
            activeTimeGoal?.let { userRepository.updateActiveTimeGoal(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createDefaultProfile(name: String): Result<UserProfile> {
        return userRepository.createDefaultProfile(name)
    }
    
    suspend fun insertUserProfile(userProfile: UserProfile): Result<Long> {
        return try {
            val id = userRepository.insertUserProfile(userProfile)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun isProfileCompleted(): Boolean {
        return userRepository.isProfileCompleted()
    }
} 