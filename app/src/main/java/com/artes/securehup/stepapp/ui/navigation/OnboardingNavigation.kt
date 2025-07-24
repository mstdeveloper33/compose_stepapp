package com.artes.securehup.stepapp.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.ui.screen.onboarding.WelcomeScreen
import com.artes.securehup.stepapp.ui.screen.onboarding.PersonalInfoScreen
import com.artes.securehup.stepapp.ui.screen.onboarding.GoalsScreen
import com.artes.securehup.stepapp.ui.viewmodel.OnboardingViewModel

sealed class OnboardingRoute(val route: String) {
    object Welcome : OnboardingRoute("welcome")
    object PersonalInfo : OnboardingRoute("personal_info")
    object Goals : OnboardingRoute("goals")
}

@Composable
fun OnboardingNavigation(
    onOnboardingComplete: () -> Unit,
    navController: NavHostController = rememberNavController(),
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Temporary storage for onboarding data
    var userData by remember { mutableStateOf<UserData?>(null) }
    
    // Observe completion
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onOnboardingComplete()
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = OnboardingRoute.Welcome.route
    ) {
        composable(OnboardingRoute.Welcome.route) {
            WelcomeScreen(
                onNavigateNext = {
                    navController.navigate(OnboardingRoute.PersonalInfo.route)
                }
            )
        }
        
        composable(OnboardingRoute.PersonalInfo.route) {
            PersonalInfoScreen(
                onNavigateNext = { name, age, height, weight, gender ->
                    userData = UserData(name, age, height, weight, gender)
                    navController.navigate(OnboardingRoute.Goals.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(OnboardingRoute.Goals.route) {
            GoalsScreen(
                onComplete = { stepGoal, calorieGoal, activeTimeGoal ->
                    userData?.let { data ->
                        viewModel.saveUserProfile(
                            name = data.name,
                            age = data.age,
                            height = data.height,
                            weight = data.weight,
                            gender = data.gender,
                            stepGoal = stepGoal,
                            calorieGoal = calorieGoal,
                            activeTimeGoal = activeTimeGoal
                        )
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
    
    // Show error dialog if needed
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Here you can show a snackbar or dialog
            // For now, we'll just clear the error after showing
            viewModel.clearError()
        }
    }
}

data class UserData(
    val name: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val gender: Gender
) 