package com.artes.securehup.stepapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.usecase.GetTodayHealthDataUseCase
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayHealthDataUseCase: GetTodayHealthDataUseCase,
    private val manageUserProfileUseCase: ManageUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load today's health data
                val todayData = getTodayHealthDataUseCase() ?: HealthData(
                    date = java.util.Date(),
                    steps = 0,
                    distance = 0.0,
                    calories = 0,
                    activeTime = 0L
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    todayHealthData = todayData
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Veriler yüklenirken hata oluştu: ${e.message}"
                )
            }
        }
        
        // Reactive user profile updates
        observeUserProfile()
    }
    
    private fun observeUserProfile() {
        viewModelScope.launch {
            manageUserProfileUseCase.getUserProfileFlow().collect { userProfile ->
                _uiState.value = _uiState.value.copy(userProfile = userProfile)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Reload today's health data
                val todayData = getTodayHealthDataUseCase() ?: HealthData(
                    date = java.util.Date(),
                    steps = 0,
                    distance = 0.0,
                    calories = 0,
                    activeTime = 0L
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    todayHealthData = todayData
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Veriler yüklenirken hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val todayHealthData: HealthData? = null,
    val error: String? = null
) 