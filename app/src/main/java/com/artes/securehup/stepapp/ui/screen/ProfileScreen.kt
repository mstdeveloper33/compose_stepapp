package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.ui.theme.*
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
    
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
                modifier = Modifier
                    .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    DarkBackground,
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                // Header
                Text(
                    text = "Kişisel Bilgileri Düzenle",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Bilgilerinizi güncelleyin",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Name Field
                CustomDialogInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "İsim",
                    placeholder = "Adınızı girin"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Age and Height Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CustomDialogInputField(
                    value = age,
                        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) age = it },
                        label = "Yaş",
                        placeholder = "25",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    
                    CustomDialogInputField(
                    value = height,
                        onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) height = it },
                        label = "Boy (cm)",
                        placeholder = "175",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Weight Field
                CustomDialogInputField(
                    value = weight,
                    onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) weight = it },
                    label = "Kilo (kg)",
                    placeholder = "70"
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Gender Selection
                Text(
                    text = "Cinsiyet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Gender.values().forEach { gender ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .background(
                                    if (selectedGender == gender) NeonGreen else DarkCard,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedGender = gender },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when(gender) {
                                    Gender.MALE -> "Erkek"
                                    Gender.FEMALE -> "Kadın"
                                    Gender.OTHER -> "Diğer"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedGender == gender) DarkBackground else TextPrimary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(DarkCard, RoundedCornerShape(24.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "İptal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(NeonGreen, RoundedCornerShape(24.dp))
                            .clickable {
                    val updatedProfile = profile.copy(
                        name = name,
                        age = age.toIntOrNull() ?: profile.age,
                        height = height.toDoubleOrNull() ?: profile.height,
                        weight = weight.toDoubleOrNull() ?: profile.weight,
                        gender = selectedGender
                    )
                    viewModel.updateProfile(updatedProfile)
                    onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kaydet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomDialogInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(DarkCard, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextTertiary,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsDialog(
    profile: UserProfile,
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var selectedGoalType by remember { mutableStateOf(0) } // 0: Adım, 1: Mesafe, 2: Kalori, 3: Aktif Süre
    var stepGoal by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var distanceGoal by remember { mutableStateOf("5") }
    var calorieGoal by remember { mutableStateOf(profile.dailyCalorieGoal.toString()) }
    var activeTimeGoal by remember { mutableStateOf(profile.dailyActiveTimeGoal.toString()) }
    var inputError by remember { mutableStateOf("") }

    // Hedef seçenekleri
    val stepGoals = listOf(
        Pair("5,000", 5000), Pair("7,500", 7500), Pair("10,000", 10000), Pair("15,000", 15000)
    )
    val distanceGoals = listOf(
        Pair("3 km", 3), Pair("5 km", 5), Pair("7 km", 7), Pair("10 km", 10)
    )
    val calorieGoals = listOf(
        Pair("200", 200), Pair("300", 300), Pair("500", 500), Pair("700", 700)
    )
    val activeTimeGoals = listOf(
        Pair("30 dk", 30), Pair("45 dk", 45), Pair("60 dk", 60), Pair("90 dk", 90)
    )

    // Maksimum değerler
    val maxValues = listOf(100_000, 30, 3000, 600) // Adım, Mesafe, Kalori, Süre

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(DarkBackground, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Hedeflerimi Düzenle",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Günlük hedeflerinizi güncelleyin",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                // Tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCard, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Adım", "Mesafe", "Kalori", "Süre").forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedGoalType == index) NeonGreen else Color.Transparent)
                                .clickable { selectedGoalType = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedGoalType == index) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (selectedGoalType == index) DarkBackground else TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Kartlar ve input
                val goals: List<Pair<String, Int>>
                val value: String
                val setValue: (String) -> Unit
                val label: String
                val unit: String
                val max: Int
                when (selectedGoalType) {
                    0 -> { goals = stepGoals; value = stepGoal; setValue = { stepGoal = it }; label = "Adım"; unit = "adım"; max = maxValues[0] }
                    1 -> { goals = distanceGoals; value = distanceGoal; setValue = { distanceGoal = it }; label = "Mesafe"; unit = "km"; max = maxValues[1] }
                    2 -> { goals = calorieGoals; value = calorieGoal; setValue = { calorieGoal = it }; label = "Kalori"; unit = "kcal"; max = maxValues[2] }
                    3 -> { goals = activeTimeGoals; value = activeTimeGoal; setValue = { activeTimeGoal = it }; label = "Süre"; unit = "dk"; max = maxValues[3] }
                    else -> { goals = stepGoals; value = stepGoal; setValue = { stepGoal = it }; label = "Adım"; unit = "adım"; max = maxValues[0] }
                }
                // Kartlar
                goals.forEachIndexed { index, (labelStr, v) ->
                    val isSelected = value == v.toString()
                    val isRecommended = when (selectedGoalType) {
                        0 -> v == 10000
                        1 -> v == 5
                        2 -> v == 300
                        3 -> v == 60
                        else -> false
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .background(
                                when {
                                    isSelected -> NeonGreen
                                    isRecommended -> NeonGreen.copy(alpha = 0.1f)
                                    else -> DarkCard
                                },
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { setValue(v.toString()) },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = labelStr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DarkBackground else TextPrimary
                            )
                            if (isRecommended && !isSelected) {
                                Text(
                                    text = "Önerilen",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeonGreen,
                                    modifier = Modifier
                                        .background(NeonGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    if (index < goals.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Input field
                CustomDialogInputField(
                    value = value,
                    onValueChange = {
                        val filtered = it.filter { c -> c.isDigit() }
                        val intVal = filtered.toIntOrNull() ?: 0
                        if (intVal > max) {
                            setValue(max.toString())
                            inputError = "Maksimum $max $unit olabilir"
                        } else {
                            setValue(filtered)
                            inputError = ""
                        }
                    },
                    label = "",
                    placeholder = "Kendi hedefini gir",
                    keyboardType = KeyboardType.Number
                )
                if (inputError.isNotEmpty()) {
                    Text(
                        text = inputError,
                        color = StatusError,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 0.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Butonlar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(DarkCard, RoundedCornerShape(24.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "İptal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(NeonGreen, RoundedCornerShape(24.dp))
                            .clickable {
                                val newStepGoal = stepGoal.toIntOrNull()?.coerceAtMost(100_000) ?: profile.dailyStepGoal
                                val newDistanceGoal = distanceGoal.toIntOrNull()?.coerceAtMost(30) ?: 5
                                val newCalorieGoal = calorieGoal.toIntOrNull()?.coerceAtMost(3000) ?: profile.dailyCalorieGoal
                                val newActiveTimeGoal = activeTimeGoal.toLongOrNull()?.coerceAtMost(600) ?: profile.dailyActiveTimeGoal
                    viewModel.updateGoals(
                        stepGoal = newStepGoal,
                        calorieGoal = newCalorieGoal,
                        activeTimeGoal = newActiveTimeGoal
                    )
                    onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kaydet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBackground
                        )
                    }
                }
            }
        }
    }
} 