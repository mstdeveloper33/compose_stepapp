package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artes.securehup.stepapp.ui.theme.*
import com.artes.securehup.stepapp.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.draw.clip

@Suppress("UNUSED_PARAMETER")
@Composable
fun GoalsScreen(
	onComplete: (Int, Int, Int, Long) -> Unit,
	onNavigateBack: () -> Unit,
	modifier: Modifier = Modifier
) {
	var stepGoal by remember { mutableStateOf("10000") }
	var calorieGoal by remember { mutableStateOf("300") }
	var activeTimeGoal by remember { mutableStateOf("60") }
	var distanceGoal by remember { mutableStateOf("5") }
	var selectedGoalIndex by remember { mutableStateOf(0) }

	val stepGoals = listOf(
		GoalData("5,000", stringResource(R.string.level_beginner), false, 5000),
		GoalData("7,500", stringResource(R.string.level_intermediate), false, 7500),
		GoalData("10,000", stringResource(R.string.level_active), true, 10000),
		GoalData("15,000", stringResource(R.string.level_athlete), false, 15000)
	)
	val distanceGoals = listOf(
		GoalData("3 km", stringResource(R.string.level_beginner), false, 3),
		GoalData("5 km", stringResource(R.string.level_intermediate), true, 5),
		GoalData("7 km", stringResource(R.string.level_active), false, 7),
		GoalData("10 km", stringResource(R.string.level_athlete), false, 10)
	)
	val calorieGoals = listOf(
		GoalData("200", stringResource(R.string.level_beginner), false, 200),
		GoalData("300", stringResource(R.string.level_intermediate), true, 300),
		GoalData("500", stringResource(R.string.level_active), false, 500),
		GoalData("700", stringResource(R.string.level_athlete), false, 700)
	)
	val activeTimeGoals = listOf(
		GoalData("30 dk", stringResource(R.string.level_beginner), false, 30),
		GoalData("45 dk", stringResource(R.string.level_intermediate), false, 45),
		GoalData("60 dk", stringResource(R.string.level_active), true, 60),
		GoalData("90 dk", stringResource(R.string.level_athlete), false, 90)
	)
	val currentGoals = when (selectedGoalIndex) {
		0 -> stepGoals
		1 -> distanceGoals
		2 -> calorieGoals
		3 -> activeTimeGoals
		else -> stepGoals
	}

	Scaffold(
		contentWindowInsets = WindowInsets.safeDrawing,
		containerColor = DarkBackground,
		bottomBar = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingLarge)
					.imePadding()
			) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(Dimensions.buttonHeightLarge)
						.background(NeonGreen, RoundedCornerShape(Dimensions.cornerRadiusXLarge))
						.clickable {
							onComplete(
								stepGoal.toIntOrNull() ?: 10000,
								calorieGoal.toIntOrNull() ?: 300,
								distanceGoal.toIntOrNull() ?: 5,
								activeTimeGoal.toLongOrNull() ?: 60
							)
						},
					contentAlignment = Alignment.Center
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.Center
					) {
						Text(
							text = stringResource(R.string.save_goals),
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
			}
		}
	) { paddingValues ->
		Column(
			modifier = modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = Dimensions.paddingLarge)
		) {
			Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
			Text(
				text = stringResource(R.string.goals_edit_title),
				fontSize = 28.sp,
				fontWeight = FontWeight.Bold,
				color = TextPrimary,
				textAlign = TextAlign.Center,
				modifier = Modifier.fillMaxWidth()
			)
			Text(
				text = stringResource(R.string.goals_edit_subtitle),
				fontSize = 16.sp,
				color = TextSecondary,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = Dimensions.paddingSmall,
						bottom = Dimensions.paddingLarge
					)
			)
			LinearProgressIndicator(
				progress = { 0.67f },
				modifier = Modifier
					.fillMaxWidth()
					.height(Dimensions.progressBarHeight)
					.clip(RoundedCornerShape(Dimensions.progressBarHeight / 2)),
				color = NeonGreen,
				trackColor = DarkCard,
			)
			Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(DarkCard, RoundedCornerShape(Dimensions.cornerRadiusLarge + Dimensions.paddingExtraSmall))
					.padding(Dimensions.paddingExtraSmall),
				horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingExtraSmall)
			) {
				GoalTypeTab(
					text = stringResource(R.string.steps),
					iconRes = "step",
					isSelected = selectedGoalIndex == 0,
					onClick = { selectedGoalIndex = 0 },
					modifier = Modifier.weight(1f)
				)
				GoalTypeTab(
					text = stringResource(R.string.distance),
					iconRes = "km",
					isSelected = selectedGoalIndex == 1,
					onClick = { selectedGoalIndex = 1 },
					modifier = Modifier.weight(1f)
				)
				GoalTypeTab(
					text = stringResource(R.string.calories),
					iconRes = "calories",
					isSelected = selectedGoalIndex == 2,
					onClick = { selectedGoalIndex = 2 },
					modifier = Modifier.weight(1f)
				)
				GoalTypeTab(
					text = stringResource(R.string.active_time),
					iconRes = "clock",
					isSelected = selectedGoalIndex == 3,
					onClick = { selectedGoalIndex = 3 },
					modifier = Modifier.weight(1f)
				)
			}
			Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
			currentGoals.forEachIndexed { index, goal ->
				val isSelected = when (selectedGoalIndex) {
					0 -> stepGoal == goal.value.toString()
					1 -> distanceGoal == goal.value.toString()
					2 -> calorieGoal == goal.value.toString()
					3 -> activeTimeGoal == goal.value.toString()
					else -> false
				}
				GoalLevelCard(
					title = goal.label,
					subtitle = goal.description,
					isRecommended = goal.isRecommended,
					isSelected = isSelected,
					onClick = {
						when (selectedGoalIndex) {
							0 -> stepGoal = goal.value.toString()
							1 -> distanceGoal = goal.value.toString()
							2 -> calorieGoal = goal.value.toString()
							3 -> activeTimeGoal = goal.value.toString()
						}
					}
				)
				if (index < currentGoals.size - 1) {
					Spacer(modifier = Modifier.height(Dimensions.paddingSmall + Dimensions.paddingExtraSmall))
				}
			}
			Spacer(modifier = Modifier.weight(1f))
		}
	}
}

@Composable
private fun GoalTypeTab(
	text: String,
	iconRes: String,
	isSelected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier
			.clip(RoundedCornerShape(Dimensions.cornerRadiusMedium))
			.background(
				if (isSelected) NeonGreen else Color.Transparent
			)
			.clickable { onClick() }
			.padding(vertical = Dimensions.paddingMedium),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Image(
				painter = painterResource(id = getDrawableResource(iconRes)),
				contentDescription = null,
				modifier = Modifier.size(Dimensions.iconSizeMedium),
				colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
					if (isSelected) DarkBackground else TextSecondary
				)
			)
			Spacer(modifier = Modifier.height(Dimensions.paddingExtraSmall))
			Text(
				text = text,
				fontSize = 14.sp,
				fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
				color = if (isSelected) DarkBackground else TextSecondary,
				textAlign = TextAlign.Center
			)
		}
	}
}

@Composable
private fun GoalLevelCard(
	title: String,
	subtitle: String,
	isRecommended: Boolean,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	val backgroundColor = when {
		isSelected -> NeonGreen
		isRecommended -> NeonGreen.copy(alpha = 0.5f)
		else -> DarkCard
	}
	val textColor = if (isSelected || isRecommended) DarkBackground else TextPrimary
	val subtitleColor = if (isSelected || isRecommended) DarkBackground.copy(alpha = 0.7f) else TextSecondary

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(Dimensions.cardHeightMedium)
			.background(
				color = backgroundColor,
				shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
			)
			.clickable { onClick() },
		contentAlignment = Alignment.CenterStart
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = Dimensions.paddingLarge),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column {
				Text(
					text = title,
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					color = textColor
				)
				Text(
					text = subtitle,
					fontSize = 14.sp,
					color = subtitleColor
				)
			}
			if (isRecommended) {
				Text(
					text = stringResource(R.string.recommended),
					fontSize = 12.sp,
					fontWeight = FontWeight.Medium,
					color = DarkBackground,
					modifier = Modifier
						.background(
							Color.Black.copy(alpha = 0.1f),
							RoundedCornerShape(Dimensions.cornerRadiusSmall)
						)
						.padding(
							horizontal = Dimensions.paddingSmall,
							vertical = Dimensions.paddingExtraSmall
						)
				)
			}
		}
	}
}

private data class GoalData(
	val label: String,
	val description: String,
	val isRecommended: Boolean,
	val value: Int
)

private fun getDrawableResource(name: String): Int {
	return when (name) {
		"step" -> R.drawable.step
		"km" -> R.drawable.km
		"calories" -> R.drawable.calories
		"clock" -> R.drawable.clock
		else -> R.drawable.step
	}
}
