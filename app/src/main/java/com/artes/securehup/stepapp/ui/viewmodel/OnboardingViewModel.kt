package com.artes.securehup.stepapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val manageUserProfileUseCase: ManageUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun saveUserProfile(
        name: String,
        age: Int,
        height: Double,
        weight: Double,
        gender: Gender,
        stepGoal: Int,
        calorieGoal: Int,
        activeTimeGoal: Long
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                Log.d("OnboardingViewModel", "Saving user profile: name=$name, age=$age")
                
                val userProfile = UserProfile(
                    name = name.trim(),
                    age = age,
                    height = height,
                    weight = weight,
                    gender = gender,
                    dailyStepGoal = stepGoal,
                    dailyCalorieGoal = calorieGoal,
                    dailyActiveTimeGoal = activeTimeGoal,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                Log.d("OnboardingViewModel", "UserProfile created: $userProfile")
                
                val result = manageUserProfileUseCase.insertUserProfile(userProfile)
                
                Log.d("OnboardingViewModel", "Insert result: ${result.isSuccess}")
                
                if (result.isSuccess) {
                    Log.d("OnboardingViewModel", "Profile saved successfully with ID: ${result.getOrNull()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isCompleted = true
                    )
                } else {
                    Log.e("OnboardingViewModel", "Failed to save profile: ${result.exceptionOrNull()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Profil kaydedilirken bir hata oluştu"
                    )
                }
            } catch (e: Exception) {
                Log.e("OnboardingViewModel", "Exception while saving profile", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Beklenmeyen bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null
) 