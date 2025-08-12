package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.util.Language
import com.artes.securehup.stepapp.ui.theme.*
import com.artes.securehup.stepapp.ui.viewmodel.ProfileViewModel
import com.artes.securehup.stepapp.R

private val CardBg = Color(0xFF181818)
private val StepColor = Color(0xFFB6E94B)
private val CalorieColor = Color(0xFFFFA940)
private val DistanceColor = Color(0xFF5B9EFF)
private val ActiveColor = Color(0xFFA259FF)

@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit = {},
    onNavigateToGoalsEdit: () -> Unit = {},
    onLanguageChanged: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    val context = LocalContext.current
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    // Dil değişikliği dinle
    LaunchedEffect(uiState.languageChanged) {
        if (uiState.languageChanged) {
            onLanguageChanged()
            viewModel.resetLanguageChangeFlag()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CardBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StepColor)
            }
        } else if (profile != null) {
            // Kişisel Bilgiler Bölümü
            PersonalInfoSection(profile = profile, onEditPersonalInfo = onNavigateToEdit)
            
            // Günlük Hedeflerim Bölümü
            DailyGoalsSection(profile = profile, onEditGoals = onNavigateToGoalsEdit)
            
            // Ayarlar Bölümü
            SettingsSection(
                onLanguageClick = { showLanguageDialog = true }
            )
            
        } else {
            Text(
                text = stringResource(R.string.profile_load_error),
                color = Color.Red
            )
        }
        
        // Dil seçimi dialog'u
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = viewModel.getCurrentLanguage(),
                availableLanguages = viewModel.getAvailableLanguages(),
                onLanguageSelected = { language ->
                    viewModel.changeLanguage(language)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
        
        // Bottom navigation için boşluk
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun PersonalInfoSection(
    profile: UserProfile,
    onEditPersonalInfo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.personal_info),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // İsim
            PersonalInfoItem(
                icon = R.drawable.habit,
                title = stringResource(R.string.name),
                value = profile.name
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Yaş
            PersonalInfoItem(
                icon = R.drawable.calendar,
                title = stringResource(R.string.age),
                value = "${profile.age} ${stringResource(R.string.age_suffix)}"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Boy
            PersonalInfoItem(
                icon = R.drawable.km,
                title = stringResource(R.string.height),
                value = "${profile.height.toInt()} ${stringResource(R.string.cm_unit)}"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Kilo
            PersonalInfoItem(
                icon = R.drawable.fire,
                title = stringResource(R.string.weight),
                value = "${profile.weight.toInt()} ${stringResource(R.string.kg_unit)}"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Cinsiyet
            PersonalInfoItem(
                icon = R.drawable.clock,
                title = stringResource(R.string.gender),
                value = when(profile.gender) {
                    Gender.MALE -> stringResource(R.string.gender_male)
                    Gender.FEMALE -> stringResource(R.string.gender_female)
                    Gender.OTHER -> stringResource(R.string.gender_other)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kişisel Bilgilerimi Düzenle Butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditPersonalInfo() }
                    .background(StepColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.habit),
                        contentDescription = null,
                        tint = StepColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.edit_personal_info),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = StepColor
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = StepColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoItem(
    icon: Int,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White
            )
        }
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun DailyGoalsSection(
    profile: UserProfile,
    onEditGoals: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StepColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.daily_goals),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Adım Hedefi
            GoalItem(
                icon = R.drawable.step,
                title = stringResource(R.string.step_goal),
                value = "${profile.dailyStepGoal}",
                unit = stringResource(R.string.step_unit),
                color = StepColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mesafe Hedefi
            GoalItem(
                icon = R.drawable.km,
                title = stringResource(R.string.distance_goal),
                value = "${profile.dailyDistanceGoal.toInt()}",
                unit = stringResource(R.string.km_unit),
                color = DistanceColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Kalori Hedefi
            GoalItem(
                icon = R.drawable.fire,
                title = stringResource(R.string.calorie_goal),
                value = "${profile.dailyCalorieGoal}",
                unit = stringResource(R.string.kcal_unit),
                color = CalorieColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Aktif Süre Hedefi
            GoalItem(
                icon = R.drawable.clock,
                title = stringResource(R.string.active_time_goal),
                value = "${profile.dailyActiveTimeGoal}",
                unit = stringResource(R.string.min_unit),
                color = ActiveColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Hedeflerimi Düzenle Butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditGoals() }
                    .background(StepColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.km),
                        contentDescription = null,
                        tint = StepColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.edit_goals),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = StepColor
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = StepColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalItem(
    icon: Int,
    title: String,
    value: String,
    unit: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White
            )
        }
        
        Text(
            text = "$value $unit",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun SettingsSection(
    onLanguageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Dil Seçenekleri
            SettingsItem(
                icon = R.drawable.habit,
                title = stringResource(R.string.language_options),
                onClick = onLanguageClick
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bizi Değerlendirin
            SettingsItem(
                icon = R.drawable.fire,
                title = stringResource(R.string.rate_us),
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Geri Bildirim
            SettingsItem(
                icon = R.drawable.calendar,
                title = stringResource(R.string.feedback),
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Arkadaşlarınla Paylaş
            SettingsItem(
                icon = R.drawable.history,
                title = stringResource(R.string.share_with_friends),
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    availableLanguages: List<Language>,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.select_language),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                availableLanguages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language.code) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == language.code,
                            onClick = { onLanguageSelected(language.code) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = StepColor,
                                unselectedColor = Color.White.copy(alpha = 0.6f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = language.displayName,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = StepColor
                )
            }
        },
        containerColor = CardBg,
        shape = RoundedCornerShape(16.dp)
    )
} 