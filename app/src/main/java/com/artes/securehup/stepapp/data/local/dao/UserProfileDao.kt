package com.artes.securehup.stepapp.data.local.dao

import androidx.room.*
import com.artes.securehup.stepapp.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity): Long
    
    @Update
    suspend fun updateUserProfile(userProfile: UserProfileEntity)
    
    @Query("UPDATE user_profile SET dailyStepGoal = :stepGoal WHERE id = 1")
    suspend fun updateStepGoal(stepGoal: Int)
    
    @Query("UPDATE user_profile SET dailyDistanceGoal = :distanceGoal WHERE id = 1")
    suspend fun updateDistanceGoal(distanceGoal: Double)
    
    @Query("UPDATE user_profile SET dailyCalorieGoal = :calorieGoal WHERE id = 1")
    suspend fun updateCalorieGoal(calorieGoal: Int)
    
    @Query("UPDATE user_profile SET dailyActiveTimeGoal = :activeTimeGoal WHERE id = 1")
    suspend fun updateActiveTimeGoal(activeTimeGoal: Long)
    
    @Query("UPDATE user_profile SET weight = :weight, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateWeight(weight: Double, updatedAt: java.util.Date)
} 