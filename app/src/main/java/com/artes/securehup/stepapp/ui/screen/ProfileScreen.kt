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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
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
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    
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
            SettingsSection()
            
        } else {
            Text(
                text = "Profil bilgileri yüklenemedi",
                color = Color.Red
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
                text = "Kişisel Bilgilerim",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // İsim
            PersonalInfoItem(
                icon = R.drawable.habit,
                title = "İsim",
                value = profile.name
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Yaş
            PersonalInfoItem(
                icon = R.drawable.calendar,
                title = "Yaş",
                value = "${profile.age} yaş"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Boy
            PersonalInfoItem(
                icon = R.drawable.km,
                title = "Boy",
                value = "${profile.height.toInt()} cm"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Kilo
            PersonalInfoItem(
                icon = R.drawable.fire,
                title = "Kilo",
                value = "${profile.weight.toInt()} kg"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Cinsiyet
            PersonalInfoItem(
                icon = R.drawable.clock,
                title = "Cinsiyet",
                value = when(profile.gender) {
                    Gender.MALE -> "Erkek"
                    Gender.FEMALE -> "Kadın"
                    Gender.OTHER -> "Diğer"
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
                        text = "Kişisel Bilgilerimi Düzenle",
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
                text = "Günlük Hedeflerim",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Adım Hedefi
            GoalItem(
                icon = R.drawable.step,
                title = "Adım Hedefi",
                value = "${profile.dailyStepGoal}",
                unit = "adım",
                color = StepColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mesafe Hedefi
            GoalItem(
                icon = R.drawable.km,
                title = "Mesafe Hedefi",
                value = "${profile.dailyDistanceGoal.toInt()}",
                unit = "km",
                color = DistanceColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Kalori Hedefi
            GoalItem(
                icon = R.drawable.fire,
                title = "Kalori Hedefi",
                value = "${profile.dailyCalorieGoal}",
                unit = "kcal",
                color = CalorieColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Aktif Süre Hedefi
            GoalItem(
                icon = R.drawable.clock,
                title = "Aktif Süre Hedefi",
                value = "${profile.dailyActiveTimeGoal}",
                unit = "dk",
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
                        text = "Hedeflerimi Düzenle",
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
private fun SettingsSection() {
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
                title = "Dil Seçenekleri",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bizi Değerlendirin
            SettingsItem(
                icon = R.drawable.fire,
                title = "Bizi Değerlendirin",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Geri Bildirim
            SettingsItem(
                icon = R.drawable.calendar,
                title = "Geri Bildirim",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Arkadaşlarınla Paylaş
            SettingsItem(
                icon = R.drawable.history,
                title = "Arkadaşınla Paylaş",
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