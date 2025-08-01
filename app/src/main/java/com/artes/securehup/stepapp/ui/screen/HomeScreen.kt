package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.ui.viewmodel.HomeViewModel
import com.artes.stepapp.R

private val StepColor = Color(0xFFB6E94B)
private val CalorieColor = Color(0xFFFFA940)
private val DistanceColor = Color(0xFF5B9EFF)
private val ActiveColor = Color(0xFFA259FF)
private val CardBg = Color(0xFF181818)

@Composable
fun HomeScreen(
    onNavigateToStats: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    val todayData = uiState.todayHealthData

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CardBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Bugün ne yapıyorsun?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        StatCard(
            icon = R.drawable.step,
            iconBg = StepColor,
            title = "Adımlar",
            value = (todayData?.steps ?: 0),
            goal = (profile?.dailyStepGoal ?: 15000),
            unit = "",
            percent = (todayData?.steps?.toFloat() ?: 0f) / (profile?.dailyStepGoal?.toFloat()
                ?: 15000f),
            cardColor = StepColor,
            onAction = { onNavigateToStats(0) }
        )
        StatCard(
            icon = R.drawable.fire,
            iconBg = CalorieColor,
            title = "Kaloriler",
            value = (todayData?.calories ?: 0),
            goal = (profile?.dailyCalorieGoal ?: 300),
            unit = "",
            percent = (todayData?.calories?.toFloat() ?: 0f) / (profile?.dailyCalorieGoal?.toFloat()
                ?: 300f),
            cardColor = CalorieColor,
            onAction = { onNavigateToStats(1) }
        )
        StatCard(
            icon = R.drawable.km,
            iconBg = DistanceColor,
            title = "Mesafe",
            value = (todayData?.distance?.toInt() ?: 0),
            goal = (profile?.dailyDistanceGoal?.toInt() ?: 7),
            unit = "",
            percent = (todayData?.distance?.toFloat() ?: 0f) / (profile?.dailyDistanceGoal?.toFloat() ?: 7f),
            cardColor = DistanceColor,
            onAction = { onNavigateToStats(2) }
        )
        StatCard(
            icon = R.drawable.clock,
            iconBg = ActiveColor,
            title = "Aktif Süre",
            value = (todayData?.activeTime ?: 0).toInt(),
            goal = (profile?.dailyActiveTimeGoal?.toInt() ?: 45),
            unit = "dk",
            percent = (todayData?.activeTime?.toFloat()
                ?: 0f) / (profile?.dailyActiveTimeGoal?.toFloat() ?: 45f),
            cardColor = ActiveColor,
            onAction = { onNavigateToStats(3) }
        )
    }
}

@Composable
private fun StatCard(
    icon: Int,
    iconBg: Color,
    title: String,
    value: Int,
    goal: Int,
    unit: String,
    percent: Float,
    cardColor: Color,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .clickable { onAction() }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚡",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Harekete geç!",
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (unit.isNotEmpty()) "$value/$goal $unit" else "$value/$goal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${((percent * 100).coerceIn(0f, 100f)).toInt()}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percent.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color.Black.copy(alpha = 0.18f),
                trackColor = Color.Black.copy(alpha = 0.08f)
            )
        }
    }
}
