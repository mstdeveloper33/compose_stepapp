package com.artes.securehup.stepapp.data.repository

import com.artes.securehup.stepapp.data.local.dao.HealthDataDao
import com.artes.securehup.stepapp.data.mapper.HealthDataMapper
import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/*
Burada kullanılan HealthRepositoryImpl sınıfı, HealthRepository arayüzünü uygular.
HealthRepository arayüzünü uygularken, HealthDataDao ve HealthDataMapper sınıflarını kullanır.
HealthDataDao sınıfı, adım verilerini veritabanına kaydeder, getirir ve günceller.
UI/ViewModel 
    ↓ (HealthData istedi)
HealthRepository Interface 
    ↓ (Implementation'a yönlendi)
HealthRepositoryImpl 
    ↓ (DAO'yu çağırdı)
HealthDataDao 
    ↓ (Entity döndü)
HealthDataMapper 
    ↓ (Domain'e çevirdi)
HealthData → UI'a döndü 
*/

@Singleton
class HealthRepositoryImpl @Inject constructor(
    private val healthDataDao: HealthDataDao,
    private val mapper: HealthDataMapper
) : HealthRepository {

    override fun getAllHealthData(): Flow<List<HealthData>> {
        return healthDataDao.getAllHealthData().map { entities ->
            entities.map { mapper.entityToDomain(it) }
        }
    }

    override suspend fun getHealthDataByDate(date: Date): HealthData? {
        val entity = healthDataDao.getHealthDataByDate(date)
        return entity?.let { mapper.entityToDomain(it) }
    }

    override suspend fun getHealthDataByDateSync(date: Date): HealthData? {
        val entity = healthDataDao.getHealthDataByDate(date)
        return entity?.let { mapper.entityToDomain(it) }
    }

    override fun getHealthDataBetweenDates(startDate: Date, endDate: Date): Flow<List<HealthData>> {
        return healthDataDao.getHealthDataBetweenDates(startDate, endDate).map { entities ->
            entities.map { mapper.entityToDomain(it) }
        }
    }
    override suspend fun getHealthDataBetweenDatesSync(startDate: Date, endDate: Date): List<HealthData> {
        val entities = healthDataDao.getHealthDataBetweenDatesSync(startDate, endDate)
        return entities.map { mapper.entityToDomain(it) }
    }

    override fun getRecentHealthData(date: Date, limit: Int): Flow<List<HealthData>> {
        return healthDataDao.getRecentHealthData(date, limit).map { entities ->
            entities.map { mapper.entityToDomain(it) }
        }
    }

    override suspend fun getTotalStepsBetweenDates(startDate: Date, endDate: Date): Int {
        return healthDataDao.getTotalStepsBetweenDates(startDate, endDate) ?: 0
    }

    override suspend fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Int {
        return healthDataDao.getTotalCaloriesBetweenDates(startDate, endDate) ?: 0
    }

    override suspend fun getTotalDistanceBetweenDates(startDate: Date, endDate: Date): Double {
        return healthDataDao.getTotalDistanceBetweenDates(startDate, endDate) ?: 0.0
    }

    override suspend fun getTotalActiveTimeBetweenDates(startDate: Date, endDate: Date): Long {
        return healthDataDao.getTotalActiveTimeBetweenDates(startDate, endDate) ?: 0L
    }

    override suspend fun insertHealthData(healthData: HealthData): Long {
        val entity = mapper.domainToEntity(healthData)
        return healthDataDao.insertHealthData(entity)
    }

    override suspend fun updateHealthData(healthData: HealthData) {
        val entity = mapper.domainToEntity(healthData)
        healthDataDao.updateHealthData(entity)
    }

    override suspend fun deleteHealthData(healthData: HealthData) {
        val entity = mapper.domainToEntity(healthData)
        healthDataDao.deleteHealthData(entity)
    }

    override suspend fun deleteOldData(cutoffDate: Date) {
        healthDataDao.deleteOldData(cutoffDate)
    }

    override suspend fun syncTodaysData(): Result<HealthData> {
        return try {
            val today = getCurrentDate()
            val existingData = getHealthDataByDate(today)
            
            if (existingData != null) {
                Result.success(existingData)
            } else {
                val newHealthData = HealthData(
                    date = today,
                    steps = 0,
                    distance = 0.0,
                    calories = 0,
                    activeTime = 0L
                )
                insertHealthData(newHealthData)
                Result.success(newHealthData)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStepsForToday(steps: Int): Result<Unit> {
        return try {
            val today = getCurrentDate()
            val existingData = getHealthDataByDate(today)
            
            if (existingData != null) {
                val updatedData = existingData.copy(steps = steps)
                updateHealthData(updatedData)
            } else {
                val newData = HealthData(date = today, steps = steps)
                insertHealthData(newData)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCaloriesForToday(calories: Int): Result<Unit> {
        return try {
            val today = getCurrentDate()
            val existingData = getHealthDataByDate(today)
            
            if (existingData != null) {
                val updatedData = existingData.copy(calories = calories)
                updateHealthData(updatedData)
            } else {
                val newData = HealthData(date = today, calories = calories)
                insertHealthData(newData)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateActiveTimeForToday(activeTime: Long): Result<Unit> {
        return try {
            val today = getCurrentDate()
            val existingData = getHealthDataByDate(today)
            
            if (existingData != null) {
                val updatedData = existingData.copy(activeTime = activeTime)
                updateHealthData(updatedData)
            } else {
                val newData = HealthData(date = today, activeTime = activeTime)
                insertHealthData(newData)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDistanceForToday(distance: Double): Result<Unit> {
        return try {
            val today = getCurrentDate()
            val existingData = getHealthDataByDate(today)
            
            if (existingData != null) {
                val updatedData = existingData.copy(distance = distance)
                updateHealthData(updatedData)
            } else {
                val newData = HealthData(date = today, distance = distance)
                insertHealthData(newData)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCurrentDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
} 