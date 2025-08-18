package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.artes.securehup.stepapp.ui.theme.*
import com.artes.securehup.stepapp.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.imePadding

@Composable
fun WelcomeScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkBackground,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingLarge)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.buttonHeightLarge)
                        .background(NeonGreen, RoundedCornerShape(Dimensions.cornerRadiusXLarge))
                        .clickable { onNavigateNext() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.get_started),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBackground
                        )
                        Spacer(modifier = Modifier.width(Dimensions.paddingSmall + Dimensions.paddingExtraSmall))
                        Box(
                            modifier = Modifier
                                .size(Dimensions.paddingXXLarge)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(Dimensions.iconSizeMedium - Dimensions.paddingExtraSmall)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
                Text(
                    text = stringResource(R.string.onboarding_ready_hint),
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimensions.paddingXXLarge))
            Box(
                modifier = Modifier
                    .size(Dimensions.appIconSize)
                    .background(NeonGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(Dimensions.iconSizeXLarge + Dimensions.paddingMedium)
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
            Text(
                text = stringResource(R.string.health_tracking_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.health_tracking_subtitle),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = TextSecondary,
                modifier = Modifier.padding(top = Dimensions.paddingSmall)
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
            ) {
                FeatureCard(
                    icon = Icons.Default.Favorite,
                    title = stringResource(R.string.daily_tracking_title),
                    description = stringResource(R.string.daily_tracking_desc)
                )
                FeatureCard(
                    icon = Icons.Default.Favorite,
                    title = stringResource(R.string.personal_goals_title),
                    description = stringResource(R.string.personal_goals_desc)
                )
                FeatureCard(
                    icon = Icons.Default.Favorite,
                    title = stringResource(R.string.detailed_analysis_title),
                    description = stringResource(R.string.detailed_analysis_desc)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            LinearProgressIndicator(
                progress = { 0.0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.progressBarHeight)
                    .clip(RoundedCornerShape(Dimensions.progressBarHeight / 2)),
                color = NeonGreen,
                trackColor = DarkCard,
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(Dimensions.cornerRadiusMedium))
            .padding(Dimensions.paddingLarge)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimensions.iconSizeXLarge)
                    .background(NeonGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(Dimensions.iconSizeMedium)
                )
            }
            
            Spacer(modifier = Modifier.width(Dimensions.paddingMedium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = Dimensions.paddingExtraSmall)
                )
            }
        }
    }
} 