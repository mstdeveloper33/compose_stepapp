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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.ui.viewmodel.ProfileViewModel

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
    var calorieGoal by remember { mutableStateOf("") }
    var activeTimeGoal by remember { mutableStateOf("") }
    
    // Initialize form with existing profile data
    LaunchedEffect(profile) {
        profile?.let {
            stepGoal = it.dailyStepGoal.toString()
            calorieGoal = it.dailyCalorieGoal.toString()
            activeTimeGoal = it.dailyActiveTimeGoal.toString()
        }
    }
    
    val isFormValid = stepGoal.isNotBlank() &&
                     calorieGoal.isNotBlank() &&
                     activeTimeGoal.isNotBlank()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hedeflerimi Düzenle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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
                        text = "💡 Hedef Önerisi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Hedeflerinizi kademeli olarak artırın. Ani değişiklikler yerine sürdürülebilir hedefler koyun.",
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
                    title = "Günlük Adım Hedefi",
                    description = "Günde kaç adım atmayı hedefliyorsunuz?",
                    emoji = "👟",
                    value = stepGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 6) stepGoal = it },
                    suffix = "adım",
                    recommendation = "Önerilen: 8,000-12,000"
                )
                
                // Calorie Goal
                GoalEditCard(
                    title = "Günlük Kalori Hedefi",
                    description = "Günde kaç kalori yakmayı hedefliyorsunuz?",
                    emoji = "🔥",
                    value = calorieGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 5) calorieGoal = it },
                    suffix = "kalori",
                    recommendation = "Önerilen: 1,800-2,500"
                )
                
                // Active Time Goal
                GoalEditCard(
                    title = "Günlük Aktif Süre Hedefi",
                    description = "Günde kaç dakika aktif olmayı hedefliyorsunuz?",
                    emoji = "⏱️",
                    value = activeTimeGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) activeTimeGoal = it },
                    suffix = "dakika",
                    recommendation = "Önerilen: 30-90"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.updateGoals(
                            stepGoal = stepGoal.toInt(),
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
                    Text("Hedefleri Kaydet")
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