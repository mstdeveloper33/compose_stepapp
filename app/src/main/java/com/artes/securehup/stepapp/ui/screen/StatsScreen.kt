package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier
) {
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
        
        // Weekly Summary
        WeeklySummarySection()
        
        // Monthly Overview
        MonthlyOverviewSection()
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WeeklySummarySection() {
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
                    value = "52,340",
                    change = "+12%",
                    isPositive = true,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatItem(
                    label = "Ortalama",
                    value = "7,477",
                    change = "-5%",
                    isPositive = false,
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
                    value = "38.2 km",
                    change = "+8%",
                    isPositive = true,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatItem(
                    label = "Kalori",
                    value = "2,130",
                    change = "+15%",
                    isPositive = true,
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
private fun MonthlyOverviewSection() {
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
                value = "12,450 adım",
                detail = "15 Ocak"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MonthlyStatRow(
                label = "Aktif Gün",
                value = "22 gün",
                detail = "Bu ay"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MonthlyStatRow(
                label = "Ortalama Adım",
                value = "8,240",
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