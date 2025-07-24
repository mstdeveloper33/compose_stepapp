package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    val todayData = uiState.todayHealthData
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Sağlık Takibi",
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
        } else if (profile != null && todayData != null) {
            // Health Stats Cards
            HealthStatsSection(todayData = todayData)
            
            // Today's Progress
            TodayProgressSection(
                todayData = todayData,
                profile = profile
            )
        } else {
            Text(
                text = "Veriler yüklenemedi",
                color = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HealthStatsSection(todayData: com.artes.securehup.stepapp.domain.model.HealthData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bugünkü Aktivite",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HealthStatItem(
                    label = "Adım",
                    value = "${todayData.steps}",
                    modifier = Modifier.weight(1f)
                )
                HealthStatItem(
                    label = "Mesafe",
                    value = "${todayData.getDistanceInKm()} km",
                    modifier = Modifier.weight(1f)
                )
                HealthStatItem(
                    label = "Kalori",
                    value = "${todayData.calories}",
                    modifier = Modifier.weight(1f)
                )
                HealthStatItem(
                    label = "Aktif Süre",
                    value = todayData.getActiveTimeInHours(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HealthStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TodayProgressSection(
    todayData: com.artes.securehup.stepapp.domain.model.HealthData,
    profile: com.artes.securehup.stepapp.domain.model.UserProfile
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Günlük Hedefler",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ProgressItem(
                label = "Adım Hedefi",
                progress = todayData.getStepProgress(profile.dailyStepGoal),
                current = "${todayData.steps}",
                target = "${profile.dailyStepGoal}"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProgressItem(
                label = "Kalori Hedefi",
                progress = todayData.getCalorieProgress(profile.dailyCalorieGoal),
                current = "${todayData.calories}",
                target = "${profile.dailyCalorieGoal}"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProgressItem(
                label = "Aktif Süre",
                progress = todayData.getActiveTimeProgress(profile.dailyActiveTimeGoal),
                current = "${todayData.activeTime} dk",
                target = "${profile.dailyActiveTimeGoal} dk"
            )
        }
    }
}

@Composable
private fun ProgressItem(
    label: String,
    progress: Float,
    current: String,
    target: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current / $target",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
    }
} 