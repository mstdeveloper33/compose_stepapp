package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import com.artes.securehup.stepapp.ui.theme.*

@Composable
fun WelcomeScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(Dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimensions.paddingXXLarge + Dimensions.paddingMedium))
        
        // App Logo/Icon
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
        
        Spacer(modifier = Modifier.height(Dimensions.paddingExtraLarge))
        
        // Welcome Title
        Text(
            text = "Sağlık Takibi",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )
        
        Text(
            text = "Sağlıklı yaşamın dijital arkadaşı",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = TextSecondary,
            modifier = Modifier.padding(top = Dimensions.paddingSmall)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.paddingXXLarge + Dimensions.paddingMedium))
        
        // Features List
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
        ) {
            FeatureCard(
                icon = Icons.Default.Favorite,
                title = "Günlük Takip",
                description = "Adım, kalori ve aktivitelerinizi kaydedin"
            )
            
            FeatureCard(
                icon = Icons.Default.Favorite,
                title = "Kişisel Hedefler",
                description = "Size özel hedefler belirleyin ve ulaşın"
            )
            
            FeatureCard(
                icon = Icons.Default.Favorite,
                title = "Detaylı Analiz",
                description = "İlerlemelerinizi grafiklerle takip edin"
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Progress indicator
        LinearProgressIndicator(
            progress = { 0.0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.progressBarHeight)
                .clip(RoundedCornerShape(Dimensions.progressBarHeight / 2)),
            color = NeonGreen,
            trackColor = DarkCard,
        )
        
        Spacer(modifier = Modifier.height(Dimensions.paddingExtraLarge))
        
        // Start Button
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
                    text = "Başlayalım",
                    fontSize = 18.sp,
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
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(Dimensions.iconSizeMedium - Dimensions.paddingExtraSmall)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
        
        Text(
            text = "Sadece birkaç adımda hazırsınız!",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
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