package com.artes.securehup.stepapp.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.ui.viewmodel.ProfileViewModel
import com.artes.securehup.stepapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    
    var stepGoal by remember { mutableStateOf("") }
    var distanceGoal by remember { mutableStateOf("") }
    var calorieGoal by remember { mutableStateOf("") }
    var activeTimeGoal by remember { mutableStateOf("") }
    
    // Initialize form with existing profile data
    LaunchedEffect(profile) {
        profile?.let {
            stepGoal = it.dailyStepGoal.toString()
            distanceGoal = it.dailyDistanceGoal.toString()
            calorieGoal = it.dailyCalorieGoal.toString()
            activeTimeGoal = it.dailyActiveTimeGoal.toString()
        }
    }
    
    val isFormValid = stepGoal.isNotBlank() &&
                     distanceGoal.isNotBlank() &&
                     calorieGoal.isNotBlank() &&
                     activeTimeGoal.isNotBlank()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_goals_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 ${stringResource(R.string.goal_suggestion)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.goal_description),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Goals Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Step Goal
                GoalEditCard(
                    title = stringResource(R.string.daily_step_goal),
                    description = stringResource(R.string.how_many_steps_daily),
                    emoji = "👟",
                    value = stepGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 6) stepGoal = it },
                    suffix = stringResource(R.string.steps_suffix),
                    recommendation = stringResource(R.string.recommended_steps)
                )
                
                // Distance Goal
                GoalEditCard(
                    title = stringResource(R.string.daily_distance_goal),
                    description = stringResource(R.string.how_many_km_daily),
                    emoji = "📍",
                    value = distanceGoal,
                    onValueChange = { if (it.matches(Regex("^\\d{0,2}(\\.\\d{0,1})?\$"))) distanceGoal = it },
                    suffix = stringResource(R.string.km_unit),
                    recommendation = stringResource(R.string.recommended_distance)
                )
                
                // Calorie Goal
                GoalEditCard(
                    title = stringResource(R.string.daily_calorie_goal),
                    description = stringResource(R.string.how_many_calories_daily),
                    emoji = "🔥",
                    value = calorieGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 5) calorieGoal = it },
                    suffix = stringResource(R.string.calories_suffix),
                    recommendation = stringResource(R.string.recommended_calories)
                )
                
                // Active Time Goal
                GoalEditCard(
                    title = stringResource(R.string.daily_active_time_goal),
                    description = stringResource(R.string.how_many_minutes_daily),
                    emoji = "⏱️",
                    value = activeTimeGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) activeTimeGoal = it },
                    suffix = stringResource(R.string.minutes_suffix),
                    recommendation = stringResource(R.string.recommended_active_time)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.updateGoals(
                            stepGoal = stepGoal.toInt(),
                            distanceGoal = distanceGoal.toDouble(),
                            calorieGoal = calorieGoal.toInt(),
                            activeTimeGoal = activeTimeGoal.toLong()
                        )
                    }
                },
                enabled = isFormValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save_goals))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Handle success message
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Show success message and navigate back
            onNavigateBack()
            viewModel.clearMessages()
        }
    }
    
    // Handle error message
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Here you can show a snackbar
            viewModel.clearMessages()
        }
    }
}

@Composable
private fun GoalEditCard(
    title: String,
    description: String,
    emoji: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    recommendation: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text(suffix) },
                singleLine = true
            )
            
            Text(
                text = recommendation,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
} 