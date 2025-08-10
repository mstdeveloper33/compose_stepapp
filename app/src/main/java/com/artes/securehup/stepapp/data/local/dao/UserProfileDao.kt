package com.artes.securehup.stepapp.data.local.dao

import androidx.room.*
import com.artes.securehup.stepapp.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    //Kullanıcı profilini getir
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>
    
    //Kullanıcı profilini eşzamanlı olarak getir
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?
    
    //Kullanıcı profilini ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity): Long
    
    //Kullanıcı profilini güncelle
    @Update
    suspend fun updateUserProfile(userProfile: UserProfileEntity)
    
    //Günlük adım hedefi güncelle
    @Query("UPDATE user_profile SET dailyStepGoal = :stepGoal WHERE id = 1")
    suspend fun updateStepGoal(stepGoal: Int)
    
    //Günlük mesafe hedefi güncelle
    @Query("UPDATE user_profile SET dailyDistanceGoal = :distanceGoal WHERE id = 1")
    suspend fun updateDistanceGoal(distanceGoal: Double)
    
    //Günlük kalori hedefi güncelle
    @Query("UPDATE user_profile SET dailyCalorieGoal = :calorieGoal WHERE id = 1")
    suspend fun updateCalorieGoal(calorieGoal: Int)
    
    //Günlük aktif zaman hedefi güncelle
    @Query("UPDATE user_profile SET dailyActiveTimeGoal = :activeTimeGoal WHERE id = 1")
    suspend fun updateActiveTimeGoal(activeTimeGoal: Long)
    
    //Kullanıcı ağırlığını güncelle
    @Query("UPDATE user_profile SET weight = :weight, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateWeight(weight: Double, updatedAt: java.util.Date)
} 

/*
Burada kullanılan UserProfileDao sınıfı, Room veritabanında kullanıcı profilini yönetir.
Kullanıcı profilini getirir, eklemek, güncellemek ve silmek için kullanılır.
Kullanıcı profilini getirirken, id = 1 olarak belirtilir.
Kullanıcı profilini eklemek, güncellemek ve silmek için, kullanıcı profilini UserProfileEntity sınıfında belirtilir.
Kullanıcı profilini güncellemek için, kullanıcı profilini UserProfileEntity sınıfında belirtilir.
Kullanıcı profilini silmek için, kullanıcı profilini UserProfileEntity sınıfında belirtilir.
Günlük adım hedefini güncellemek için, günlük adım hedefini updateStepGoal fonksiyonunda belirtilir.
Günlük mesafe hedefini güncellemek için, günlük mesafe hedefini updateDistanceGoal fonksiyonunda belirtilir.
Günlük kalori hedefini güncellemek için, günlük kalori hedefini updateCalorieGoal fonksiyonunda belirtilir.
Günlük aktif zaman hedefini güncellemek için, günlük aktif zaman hedefini updateActiveTimeGoal fonksiyonunda belirtilir.
Kullanıcı ağırlığını güncellemek için, kullanıcı ağırlığını updateWeight fonksiyonunda belirtilir.
Burada kullanılan HealthDataDao sınıfı, Room veritabanında adım verilerini yönetir.
Adım verilerini getirir, eklemek, güncellemek ve silmek için kullanılır.
Ayrıca buradaki dao yapıları repository katmanına refaranstır yani repository katmanında kullanılır.

*/