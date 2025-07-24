package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit = {},
    onNavigateToGoalsEdit: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    
    var showPersonalInfoDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Profil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (profile != null) {
            // User Info Card (Clickable)
            UserInfoSection(
                profile = profile,
                onClick = { showPersonalInfoDialog = true }
            )
            
            // Goals Settings (Clickable)
            GoalsSection(
                profile = profile,
                onClick = { showGoalsDialog = true }
            )
            
            // BMI Info
            BMISection(profile = profile)
            
        } else {
            Text(
                text = "Profil bilgileri yüklenemedi",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
    
    // Personal Info Dialog
    if (showPersonalInfoDialog && profile != null) {
        PersonalInfoDialog(
            profile = profile,
            viewModel = viewModel,
            onDismiss = { showPersonalInfoDialog = false }
        )
    }
    
    // Goals Dialog
    if (showGoalsDialog && profile != null) {
        GoalsDialog(
            profile = profile,
            viewModel = viewModel,
            onDismiss = { showGoalsDialog = false }
        )
    }
}

@Composable
private fun UserInfoSection(
    profile: com.artes.securehup.stepapp.domain.model.UserProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kişisel Bilgiler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Düzenle",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            UserInfoRow(label = "İsim", value = profile.name)
            UserInfoRow(label = "Yaş", value = "${profile.age}")
            UserInfoRow(label = "Boy", value = "${profile.height.toInt()} cm")
            UserInfoRow(label = "Kilo", value = "${profile.weight.toInt()} kg")
            UserInfoRow(label = "Cinsiyet", value = when(profile.gender) {
                com.artes.securehup.stepapp.domain.model.Gender.MALE -> "Erkek"
                com.artes.securehup.stepapp.domain.model.Gender.FEMALE -> "Kadın"
                com.artes.securehup.stepapp.domain.model.Gender.OTHER -> "Diğer"
            })
        }
    }
}

@Composable
private fun UserInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun GoalsSection(
    profile: com.artes.securehup.stepapp.domain.model.UserProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Günlük Hedefler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Düzenle",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            GoalRow(
                label = "Adım Hedefi",
                value = "${profile.dailyStepGoal}",
                unit = "adım"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            GoalRow(
                label = "Kalori Hedefi",
                value = "${profile.dailyCalorieGoal}",
                unit = "kalori"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            GoalRow(
                label = "Aktif Süre",
                value = "${profile.dailyActiveTimeGoal}",
                unit = "dakika"
            )
        }
    }
}

@Composable
private fun GoalRow(
    label: String,
    value: String,
    unit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " $unit",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BMISection(profile: com.artes.securehup.stepapp.domain.model.UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sağlık Göstergeleri",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "%.1f".format(profile.getBMI()),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "BMI",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = when(profile.getBMICategory()) {
                            com.artes.securehup.stepapp.domain.model.BMICategory.UNDERWEIGHT -> "Zayıf"
                            com.artes.securehup.stepapp.domain.model.BMICategory.NORMAL -> "Normal"
                            com.artes.securehup.stepapp.domain.model.BMICategory.OVERWEIGHT -> "Fazla kilolu"
                            com.artes.securehup.stepapp.domain.model.BMICategory.OBESE -> "Obez"
                        },
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${profile.getBasalMetabolicRate()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "BMR",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "kalori/gün",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalInfoDialog(
    profile: UserProfile,
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var age by remember { mutableStateOf(profile.age.toString()) }
    var height by remember { mutableStateOf(profile.height.toString()) }
    var weight by remember { mutableStateOf(profile.weight.toString()) }
    var selectedGender by remember { mutableStateOf(profile.gender) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Kişisel Bilgileri Düzenle")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("İsim") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Age
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Yaş") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Height
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Boy (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Weight
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Kilo (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Gender
                Text(
                    text = "Cinsiyet",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Column {
                    Gender.values().forEach { gender ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedGender == gender),
                                    onClick = { selectedGender = gender },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedGender == gender),
                                onClick = null
                            )
                            Text(
                                text = when(gender) {
                                    Gender.MALE -> "Erkek"
                                    Gender.FEMALE -> "Kadın"
                                    Gender.OTHER -> "Diğer"
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedProfile = profile.copy(
                        name = name,
                        age = age.toIntOrNull() ?: profile.age,
                        height = height.toDoubleOrNull() ?: profile.height,
                        weight = weight.toDoubleOrNull() ?: profile.weight,
                        gender = selectedGender
                    )
                    viewModel.updateProfile(updatedProfile)
                    onDismiss()
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsDialog(
    profile: UserProfile,
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var stepGoal by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var calorieGoal by remember { mutableStateOf(profile.dailyCalorieGoal.toString()) }
    var activeTimeGoal by remember { mutableStateOf(profile.dailyActiveTimeGoal.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Günlük Hedefleri Düzenle")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = stepGoal,
                    onValueChange = { stepGoal = it },
                    label = { Text("Adım Hedefi") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = calorieGoal,
                    onValueChange = { calorieGoal = it },
                    label = { Text("Kalori Hedefi") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = activeTimeGoal,
                    onValueChange = { activeTimeGoal = it },
                    label = { Text("Aktif Süre (dakika)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newStepGoal = stepGoal.toIntOrNull() ?: profile.dailyStepGoal
                    val newCalorieGoal = calorieGoal.toIntOrNull() ?: profile.dailyCalorieGoal
                    val newActiveTimeGoal = activeTimeGoal.toLongOrNull() ?: profile.dailyActiveTimeGoal
                    
                    viewModel.updateGoals(
                        stepGoal = newStepGoal,
                        calorieGoal = newCalorieGoal,
                        activeTimeGoal = newActiveTimeGoal
                    )
                    onDismiss()
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
} 