package com.artes.securehup.stepapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ProfileViewModel @Inject constructor(
    private val manageUserProfileUseCase: ManageUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            manageUserProfileUseCase.getUserProfileFlow().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userProfile = profile
                )
            }
        }
    }

    fun updateProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val profileWithTimestamp = updatedProfile.copy(updatedAt = Date())
                val result = manageUserProfileUseCase.updateUserProfile(profileWithTimestamp)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Profil başarıyla güncellendi"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Profil güncellenirken hata oluştu"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Beklenmeyen bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun updateGoals(stepGoal: Int, distanceGoal: Double, calorieGoal: Int, activeTimeGoal: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = manageUserProfileUseCase.updateGoals(
                    stepGoal = stepGoal,
                    distanceGoal = distanceGoal,
                    calorieGoal = calorieGoal,
                    activeTimeGoal = activeTimeGoal
                )
                
                if (result.isSuccess) {
                    // Flow otomatik olarak güncellenecek, sadece success mesajı göster
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Hedefler başarıyla güncellendi"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Hedefler güncellenirken hata oluştu"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Beklenmeyen bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun updateWeight(newWeight: Double) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.userProfile
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(
                    weight = newWeight,
                    updatedAt = Date()
                )
                updateProfile(updatedProfile)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    fun checkProfileExists(): Boolean {
        return _uiState.value.userProfile != null
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val successMessage: String? = null
) 