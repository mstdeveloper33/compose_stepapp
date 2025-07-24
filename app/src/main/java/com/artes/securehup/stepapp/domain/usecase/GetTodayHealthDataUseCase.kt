package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GetTodayHealthDataUseCase @Inject constructor(
    private val healthRepository: HealthRepository
) {
    suspend operator fun invoke(): HealthData? {
        val today = getCurrentDate()
        return healthRepository.getHealthDataByDate(today)
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