package com.artes.securehup.stepapp.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.R
import com.artes.securehup.stepapp.ui.viewmodel.HomeViewModel

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

    val configuration = LocalConfiguration.current
    val dimensions = remember(configuration) {
        ResponsiveDimensions.calculate(configuration)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CardBg)
            .padding(
                start = dimensions.horizontalPadding,
                end = dimensions.horizontalPadding,
                top = dimensions.topPadding,
                bottom = dimensions.bottomPadding
            )
    ) {
        // Header
        Text(
            text = stringResource(R.string.lets_get_started),
            fontSize = dimensions.headerFontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensions.headerBottomMargin),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Stat Cards - Ekrana tam sığacak şekilde
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            val statCards = listOf(
                StatCardData(
                    icon = R.drawable.step,
                    iconBg = StepColor,
                    title = stringResource(R.string.steps),
                    value = (todayData?.steps ?: 0),
                    goal = (profile?.dailyStepGoal ?: 15000),
                    unit = "",
                    percent = (todayData?.steps?.toFloat() ?: 0f) / (profile?.dailyStepGoal?.toFloat() ?: 15000f),
                    cardColor = StepColor,
                    onAction = { onNavigateToStats(0) }
                ),
                StatCardData(
                    icon = R.drawable.fire,
                    iconBg = CalorieColor,
                    title = stringResource(R.string.calories),
                    value = (todayData?.calories ?: 0),
                    goal = (profile?.dailyCalorieGoal ?: 300),
                    unit = "",
                    percent = (todayData?.calories?.toFloat() ?: 0f) / (profile?.dailyCalorieGoal?.toFloat() ?: 300f),
                    cardColor = CalorieColor,
                    onAction = { onNavigateToStats(1) }
                ),
                StatCardData(
                    icon = R.drawable.km,
                    iconBg = DistanceColor,
                    title = stringResource(R.string.distance),
                    value = (todayData?.distance?.toInt() ?: 0),
                    goal = (profile?.dailyDistanceGoal?.toInt() ?: 7),
                    unit = "",
                    percent = (todayData?.distance?.toFloat() ?: 0f) / (profile?.dailyDistanceGoal?.toFloat() ?: 7f),
                    cardColor = DistanceColor,
                    onAction = { onNavigateToStats(2) }
                ),
                StatCardData(
                    icon = R.drawable.clock,
                    iconBg = ActiveColor,
                    title = stringResource(R.string.active_time),
                    value = (todayData?.activeTime ?: 0).toInt(),
                    goal = (profile?.dailyActiveTimeGoal?.toInt() ?: 45),
                    unit = stringResource(R.string.min_unit),
                    percent = (todayData?.activeTime?.toFloat() ?: 0f) / (profile?.dailyActiveTimeGoal?.toFloat() ?: 45f),
                    cardColor = ActiveColor,
                    onAction = { onNavigateToStats(3) }
                )
            )

            statCards.forEach { cardData ->
                StatCard(
                    data = cardData,
                    dimensions = dimensions
                )
            }
        }
    }
}

data class StatCardData(
    val icon: Int,
    val iconBg: Color,
    val title: String,
    val value: Int,
    val goal: Int,
    val unit: String,
    val percent: Float,
    val cardColor: Color,
    val onAction: () -> Unit
)

data class ResponsiveDimensions(
    val horizontalPadding: Dp,
    val topPadding: Dp,
    val bottomPadding: Dp,
    val cardSpacing: Dp,
    val cardHeight: Dp,
    val cardPadding: Dp,
    val cornerRadius: Dp,
    val iconSize: Dp,
    val iconInnerSize: Dp,
    val headerFontSize: androidx.compose.ui.unit.TextUnit,
    val titleFontSize: androidx.compose.ui.unit.TextUnit,
    val headerBottomMargin: Dp
) {
    companion object {
        fun calculate(configuration: Configuration): ResponsiveDimensions {
            val screenWidth = configuration.screenWidthDp
            val screenHeight = configuration.screenHeightDp
            
            // Dinamik hesaplamalar - ekrana tam sığacak şekilde
            val availableHeight = screenHeight - 160 // Status bar, header, bottom bar, padding için rezerv
            val cardHeight = (availableHeight / 4.5f).coerceIn(100f, 160f)
            val cardSpacing = (availableHeight * 0.03f).coerceIn(8f, 20f)
            val topPadding = (screenHeight * 0.02f).coerceIn(16f, 24f)
            val bottomPadding = (screenHeight * 0.02f).coerceIn(20f, 20f)
            
            val isSmallDevice = screenHeight < 600 || screenWidth < 360
            val isTablet = screenWidth > 600 && screenHeight > 800
            val isLargePhone = screenHeight > 800 && !isTablet

            return when {
                isSmallDevice -> ResponsiveDimensions(
                    horizontalPadding = 16.dp,
                    topPadding = topPadding.dp,
                    bottomPadding = bottomPadding.dp,
                    cardSpacing = cardSpacing.dp,
                    cardHeight = cardHeight.dp,
                    cardPadding = 18.dp,
                    cornerRadius = 22.dp,
                    iconSize = 40.dp,
                    iconInnerSize = 22.dp,
                    headerFontSize = 20.sp,
                    titleFontSize = 16.sp,
                    headerBottomMargin = 16.dp
                )
                isTablet -> ResponsiveDimensions(
                    horizontalPadding = (screenWidth * 0.08f).dp,
                    topPadding = (topPadding * 1.5f).dp,
                    bottomPadding = bottomPadding.dp,
                    cardSpacing = (cardSpacing * 1.3f).dp,
                    cardHeight = (cardHeight * 1.2f).dp,
                    cardPadding = 28.dp,
                    cornerRadius = 28.dp,
                    iconSize = 56.dp,
                    iconInnerSize = 32.dp,
                    headerFontSize = 28.sp,
                    titleFontSize = 22.sp,
                    headerBottomMargin = 32.dp
                )
                isLargePhone -> ResponsiveDimensions(
                    horizontalPadding = 20.dp,
                    topPadding = (topPadding * 1.2f).dp,
                    bottomPadding = bottomPadding.dp,
                    cardSpacing = (cardSpacing * 1.1f).dp,
                    cardHeight = (cardHeight * 1.1f).dp,
                    cardPadding = 24.dp,
                    cornerRadius = 26.dp,
                    iconSize = 50.dp,
                    iconInnerSize = 28.dp,
                    headerFontSize = 25.sp,
                    titleFontSize = 21.sp,
                    headerBottomMargin = 24.dp
                )
                else -> ResponsiveDimensions(
                    horizontalPadding = 18.dp,
                    topPadding = topPadding.dp,
                    bottomPadding = bottomPadding.dp,
                    cardSpacing = cardSpacing.dp,
                    cardHeight = cardHeight.dp,
                    cardPadding = 20.dp,
                    cornerRadius = 24.dp,
                    iconSize = 46.dp,
                    iconInnerSize = 24.dp,
                    headerFontSize = 23.sp,
                    titleFontSize = 19.sp,
                    headerBottomMargin = 20.dp
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    data: StatCardData,
    dimensions: ResponsiveDimensions
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.cardHeight)
            .background(data.cardColor, RoundedCornerShape(dimensions.cornerRadius))
            .padding(dimensions.cardPadding)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSize)
                        .background(data.iconBg.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = data.icon),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(dimensions.iconInnerSize)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = data.title,
                    fontSize = dimensions.titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.weight(1f))

                val (ctaEmoji, ctaText) = getMotivationMessage(data.title, data.percent)
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .clickable { data.onAction() }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ctaEmoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = ctaText,
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
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
                    text = if (data.unit.isNotEmpty()) "${data.value}/${data.goal} ${data.unit}" else "${data.value}/${data.goal}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${((data.percent * 100).coerceIn(0f, 100f)).toInt()}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { data.percent.coerceIn(0f, 1f) },
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

@Composable
private fun getMotivationMessage(
    title: String,
    percent: Float
): Pair<String, String> {
    val p = (percent * 100).coerceIn(0f, 100f)
    val stepsTitle = stringResource(R.string.steps)
    val caloriesTitle = stringResource(R.string.calories)
    val distanceTitle = stringResource(R.string.distance)
    val activeTimeTitle = stringResource(R.string.active_time)

    return when (title) {
        stepsTitle -> when {
            p < 10f -> "🚶" to stringResource(R.string.motivation_start)
            p < 40f -> "💪" to stringResource(R.string.motivation_continue)
            p < 70f -> "⚡" to stringResource(R.string.motivation_close)
            p < 100f -> "🏁" to stringResource(R.string.motivation_final)
            else -> "🎉" to stringResource(R.string.motivation_great)
        }
        caloriesTitle -> when {
            p < 10f -> "🔥" to stringResource(R.string.motivation_warm_up_steps)
            p < 40f -> "🥵" to stringResource(R.string.motivation_tempo_calories)
            p < 70f -> "⚡" to stringResource(R.string.motivation_continue)
            p < 100f -> "🏁" to stringResource(R.string.motivation_finish_goal)
            else -> "🎯" to stringResource(R.string.motivation_awesome)
        }
        distanceTitle -> when {
            p < 10f -> "🗺️" to stringResource(R.string.motivation_short_walk_distance)
            p < 40f -> "🚶‍♂️" to stringResource(R.string.motivation_continue)
            p < 70f -> "🏃" to stringResource(R.string.motivation_close)
            p < 100f -> "🏁" to stringResource(R.string.motivation_finish_goal)
            else -> "🌟" to stringResource(R.string.motivation_good_work)
        }
        activeTimeTitle -> when {
            p < 10f -> "⏱️" to stringResource(R.string.motivation_5min)
            p < 40f -> "💪" to stringResource(R.string.motivation_keep_going)
            p < 70f -> "⚡" to stringResource(R.string.motivation_rhythm)
            p < 100f -> "🏁" to stringResource(R.string.motivation_final_minutes)
            else -> "🎉" to stringResource(R.string.motivation_done)
        }
        else -> "⚡" to stringResource(R.string.motivation_get_moving)
    }
}
