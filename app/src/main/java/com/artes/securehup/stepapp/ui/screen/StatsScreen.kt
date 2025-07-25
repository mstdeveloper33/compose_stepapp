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
import com.artes.securehup.stepapp.ui.viewmodel.StatsViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "İstatistikler",
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
        } else {
            // Weekly Summary
            uiState.weeklyStats?.let { weeklyStats ->
                WeeklySummarySection(weeklyStats = weeklyStats)
            }
            
            // Monthly Overview
            uiState.monthlyStats?.let { monthlyStats ->
                MonthlyOverviewSection(monthlyStats = monthlyStats)
            }
            
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WeeklySummarySection(
    weeklyStats: com.artes.securehup.stepapp.ui.viewmodel.WeeklyStatsData
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bu Hafta",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeeklyStatItem(
                    label = "Toplam Adım",
                    value = NumberFormat.getNumberInstance(Locale("tr", "TR")).format(weeklyStats.totalSteps),
                    change = "${if (weeklyStats.stepChange >= 0) "+" else ""}${weeklyStats.stepChange}%",
                    isPositive = weeklyStats.stepChange >= 0,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatItem(
                    label = "Ortalama",
                    value = NumberFormat.getNumberInstance(Locale("tr", "TR")).format(weeklyStats.averageSteps),
                    change = "${if (weeklyStats.averageStepsChange >= 0) "+" else ""}${weeklyStats.averageStepsChange}%",
                    isPositive = weeklyStats.averageStepsChange >= 0,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeeklyStatItem(
                    label = "Mesafe",
                    value = "${String.format(Locale("tr", "TR"), "%.1f", weeklyStats.totalDistance)} km",
                    change = "${if (weeklyStats.distanceChange >= 0) "+" else ""}${weeklyStats.distanceChange}%",
                    isPositive = weeklyStats.distanceChange >= 0,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatItem(
                    label = "Kalori",
                    value = NumberFormat.getNumberInstance(Locale("tr", "TR")).format(weeklyStats.totalCalories),
                    change = "${if (weeklyStats.calorieChange >= 0) "+" else ""}${weeklyStats.calorieChange}%",
                    isPositive = weeklyStats.calorieChange >= 0,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeeklyStatItem(
    label: String,
    value: String,
    change: String,
    isPositive: Boolean,
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
        Text(
            text = change,
            fontSize = 10.sp,
            color = if (isPositive) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun MonthlyOverviewSection(
    monthlyStats: com.artes.securehup.stepapp.ui.viewmodel.MonthlyStatsData
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Aylık Genel Bakış",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MonthlyStatRow(
                label = "En İyi Gün",
                value = monthlyStats.bestDay,
                detail = monthlyStats.bestDayDate
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MonthlyStatRow(
                label = "Aktif Gün",
                value = "${monthlyStats.activeDays} gün",
                detail = "Bu ay (${monthlyStats.totalDays} günden)"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MonthlyStatRow(
                label = "Ortalama Adım",
                value = NumberFormat.getNumberInstance(Locale("tr", "TR")).format(monthlyStats.averageSteps),
                detail = "Günlük"
            )
        }
    }
}

@Composable
private fun MonthlyStatRow(
    label: String,
    value: String,
    detail: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = detail,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
} 