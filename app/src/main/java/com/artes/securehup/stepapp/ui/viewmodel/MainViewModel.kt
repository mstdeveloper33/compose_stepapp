package com.artes.securehup.stepapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val manageUserProfileUseCase: ManageUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkUserProfile()
    }

    private fun checkUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                Log.d("MainViewModel", "Checking if user profile is completed...")
                
                val isProfileCompleted = manageUserProfileUseCase.isProfileCompleted()
                
                Log.d("MainViewModel", "Profile completed: $isProfileCompleted")
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isProfileCompleted = isProfileCompleted,
                    isInitialized = true
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error checking profile", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isProfileCompleted = false,
                    isInitialized = true,
                    error = "Profil kontrol edilirken hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun onOnboardingCompleted() {
        Log.d("MainViewModel", "Onboarding completed, updating state")
        _uiState.value = _uiState.value.copy(isProfileCompleted = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val isProfileCompleted: Boolean = false,
    val isInitialized: Boolean = false,
    val error: String? = null
) 