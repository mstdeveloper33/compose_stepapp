package com.artes.securehup.stepapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.usecase.GetTodayHealthDataUseCase
import com.artes.securehup.stepapp.domain.usecase.GetWeeklyStatsUseCase
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getTodayHealthDataUseCase: GetTodayHealthDataUseCase,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val manageUserProfileUseCase: ManageUserProfileUseCase,
    private val healthRepository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Kullanıcı profili al
                val userProfile = manageUserProfileUseCase.getUserProfile()
                
                // Bu haftanın verilerini al
                val weeklyStats = calculateWeeklyStats()
                
                // Aylık verileri al
                val monthlyStats = calculateMonthlyStats()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userProfile = userProfile,
                    weeklyStats = weeklyStats,
                    monthlyStats = monthlyStats
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "İstatistikler yüklenirken hata oluştu: ${e.message}"
                )
            }
        }
    }

    private suspend fun calculateWeeklyStats(): WeeklyStatsData {
        val calendar = Calendar.getInstance()
        
        // Bu haftanın başlangıcını bul (Pazartesi)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val weekStart = calendar.time
        
        // Bu haftanın sonunu bul (Pazar)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val weekEnd = calendar.time
        
        // Bu haftanın verilerini al
        val thisWeekData = healthRepository.getHealthDataBetweenDatesSync(weekStart, weekEnd)
        
        // Geçen haftanın verilerini al (karşılaştırma için)
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val lastWeekStart = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val lastWeekEnd = calendar.time
        
        val lastWeekData = healthRepository.getHealthDataBetweenDatesSync(lastWeekStart, lastWeekEnd)
        
        // Hesaplamalar
        val thisWeekSteps = thisWeekData.sumOf { it.steps }
        val thisWeekDistance = thisWeekData.sumOf { it.distance }
        val thisWeekCalories = thisWeekData.sumOf { it.calories }
        val thisWeekActiveTime = thisWeekData.sumOf { it.activeTime }
        
        val lastWeekSteps = lastWeekData.sumOf { it.steps }
        val lastWeekDistance = lastWeekData.sumOf { it.distance }
        val lastWeekCalories = lastWeekData.sumOf { it.calories }
        
        // Değişim oranları
        val stepChange = calculatePercentageChange(lastWeekSteps, thisWeekSteps)
        val distanceChange = calculatePercentageChange(lastWeekDistance, thisWeekDistance)
        val calorieChange = calculatePercentageChange(lastWeekCalories, thisWeekCalories)
        
        // Ortalamalar
        val averageSteps = if (thisWeekData.isNotEmpty()) thisWeekSteps / thisWeekData.size else 0
        val averageStepsChange = calculatePercentageChange(
            if (lastWeekData.isNotEmpty()) lastWeekSteps / lastWeekData.size else 0,
            averageSteps
        )
        
        return WeeklyStatsData(
            totalSteps = thisWeekSteps,
            stepChange = stepChange,
            averageSteps = averageSteps,
            averageStepsChange = averageStepsChange,
            totalDistance = thisWeekDistance,
            distanceChange = distanceChange,
            totalCalories = thisWeekCalories,
            calorieChange = calorieChange,
            totalActiveTime = thisWeekActiveTime
        )
    }

    private suspend fun calculateMonthlyStats(): MonthlyStatsData {
        val calendar = Calendar.getInstance()
        
        // Bu ayın başlangıcı
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = calendar.time
        
        // Bu ayın sonu
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val monthEnd = calendar.time
        
        // Bu ayın verilerini al
        val monthData = healthRepository.getHealthDataBetweenDatesSync(monthStart, monthEnd)
        
        if (monthData.isEmpty()) {
            return MonthlyStatsData(
                bestDay = "Veri yok",
                bestDaySteps = 0,
                bestDayDate = "—",
                activeDays = 0,
                totalDays = calendar.get(Calendar.DAY_OF_MONTH),
                averageSteps = 0,
                totalSteps = 0,
                totalDistance = 0.0,
                totalCalories = 0
            )
        }
        
        // En iyi günü bul
        val bestDayData = monthData.maxByOrNull { it.steps }
        val dateFormatter = SimpleDateFormat("d MMMM", Locale("tr", "TR"))
        
        // Aktif günleri say (en az 1000 adım atılan günler)
        val activeDays = monthData.count { it.steps >= 1000 }
        val totalDays = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Ortalamalar
        val totalSteps = monthData.sumOf { it.steps }
        val averageSteps = if (monthData.isNotEmpty()) totalSteps / monthData.size else 0
        
        val totalDistance = monthData.sumOf { it.distance }
        val totalCalories = monthData.sumOf { it.calories }
        
        return MonthlyStatsData(
            bestDay = if (bestDayData != null) "${bestDayData.steps} adım" else "Veri yok",
            bestDaySteps = bestDayData?.steps ?: 0,
            bestDayDate = bestDayData?.let { dateFormatter.format(it.date) } ?: "—",
            activeDays = activeDays,
            totalDays = totalDays,
            averageSteps = averageSteps,
            totalSteps = totalSteps,
            totalDistance = totalDistance,
            totalCalories = totalCalories
        )
    }

    private fun calculatePercentageChange(old: Int, new: Int): Int {
        if (old == 0) return if (new > 0) 100 else 0
        return ((new - old).toDouble() / old * 100).roundToInt()
    }

    private fun calculatePercentageChange(old: Double, new: Double): Int {
        if (old == 0.0) return if (new > 0) 100 else 0
        return ((new - old) / old * 100).roundToInt()
    }

    fun refreshStats() {
        loadStats()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class StatsUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val weeklyStats: WeeklyStatsData? = null,
    val monthlyStats: MonthlyStatsData? = null,
    val error: String? = null
)

data class WeeklyStatsData(
    val totalSteps: Int,
    val stepChange: Int,
    val averageSteps: Int,
    val averageStepsChange: Int,
    val totalDistance: Double,
    val distanceChange: Int,
    val totalCalories: Int,
    val calorieChange: Int,
    val totalActiveTime: Long
)

data class MonthlyStatsData(
    val bestDay: String,
    val bestDaySteps: Int,
    val bestDayDate: String,
    val activeDays: Int,
    val totalDays: Int,
    val averageSteps: Int,
    val totalSteps: Int,
    val totalDistance: Double,
    val totalCalories: Int
) 