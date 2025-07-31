package com.artes.securehup.stepapp.data.repository

import com.artes.securehup.stepapp.data.local.dao.UserProfileDao
import com.artes.securehup.stepapp.data.mapper.UserProfileMapper
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val mapper: UserProfileMapper
) : UserRepository {

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.let { mapper.entityToDomain(it) }
        }
    }

    override suspend fun getUserProfileSync(): UserProfile? {
        val entity = userProfileDao.getUserProfileSync()
        return entity?.let { mapper.entityToDomain(it) }
    }

    override suspend fun insertUserProfile(userProfile: UserProfile): Long {
        val entity = mapper.domainToEntity(userProfile)
        return userProfileDao.insertUserProfile(entity)
    }

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        val entity = mapper.domainToEntity(userProfile.copy(updatedAt = Date()))
        userProfileDao.updateUserProfile(entity)
    }

    override suspend fun updateStepGoal(stepGoal: Int) {
        userProfileDao.updateStepGoal(stepGoal)
    }

    override suspend fun updateDistanceGoal(distanceGoal: Double) {
        userProfileDao.updateDistanceGoal(distanceGoal)
    }

    override suspend fun updateCalorieGoal(calorieGoal: Int) {
        userProfileDao.updateCalorieGoal(calorieGoal)
    }

    override suspend fun updateActiveTimeGoal(activeTimeGoal: Long) {
        userProfileDao.updateActiveTimeGoal(activeTimeGoal)
    }

    override suspend fun updateWeight(weight: Double, updatedAt: Date) {
        userProfileDao.updateWeight(weight, updatedAt)
    }

    override suspend fun isProfileCompleted(): Boolean {
        return try {
            val profile = getUserProfileSync()
            profile != null && profile.name.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun createDefaultProfile(name: String): Result<UserProfile> {
        return try {
            val defaultProfile = UserProfile(
                name = name,
                age = 25,
                height = 170.0,
                weight = 70.0,
                gender = Gender.OTHER,
                dailyStepGoal = 10000,
                dailyDistanceGoal = 7.0,
                dailyCalorieGoal = 2000,
                dailyActiveTimeGoal = 60
            )
            
            insertUserProfile(defaultProfile)
            Result.success(defaultProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 