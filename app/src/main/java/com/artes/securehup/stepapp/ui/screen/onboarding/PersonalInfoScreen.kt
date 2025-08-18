package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.ui.theme.*
import com.artes.securehup.stepapp.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.imePadding

@Suppress("UNUSED_PARAMETER")
@Composable
fun PersonalInfoScreen(
    onNavigateNext: (String, Int, Double, Double, Gender) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }

    val isFormValid = name.isNotBlank() &&
        age.isNotBlank() &&
        height.isNotBlank() &&
        weight.isNotBlank() &&
        selectedGender != null

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
                        .background(
                            if (isFormValid) NeonGreen else DarkCard,
                            RoundedCornerShape(Dimensions.cornerRadiusXLarge)
                        )
                        .clickable(enabled = isFormValid) {
                            if (isFormValid) {
                                onNavigateNext(
                                    name,
                                    age.toInt(),
                                    height.toDouble(),
                                    weight.toDouble(),
                                    selectedGender!!
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Devam Et",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isFormValid) DarkBackground else TextTertiary
                        )
                        Spacer(modifier = Modifier.width(Dimensions.paddingSmall + Dimensions.paddingExtraSmall))
                        Box(
                            modifier = Modifier
                                .size(Dimensions.paddingXXLarge)
                                .background(
                                    if (isFormValid) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = if (isFormValid) DarkBackground else TextTertiary,
                                modifier = Modifier.size(Dimensions.iconSizeMedium - Dimensions.paddingExtraSmall)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimensions.paddingXXLarge))
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
                text = stringResource(R.string.personal_info_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.personal_info_subtitle),
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
                progress = { 0.33f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.progressBarHeight),
                color = NeonGreen,
                trackColor = DarkCard,
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
            CustomInputField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.name),
                placeholder = stringResource(R.string.enter_your_name)
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                CustomInputField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) age = it },
                    label = stringResource(R.string.age),
                    placeholder = "25",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                CustomInputField(
                    value = height,
                    onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) height = it },
                    label = stringResource(R.string.height) + " (" + stringResource(R.string.cm_unit) + ")",
                    placeholder = "175",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
            CustomInputField(
                value = weight,
                onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) weight = it },
                label = stringResource(R.string.weight) + " (" + stringResource(R.string.kg_unit) + ")",
                placeholder = "70"
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
            Text(
                text = stringResource(R.string.gender),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = Dimensions.paddingMedium)
            )
            GenderSegmentedControl(
                selectedGender = selectedGender,
                onSelect = { selectedGender = it }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = Dimensions.paddingSmall)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.inputFieldHeight)
                .background(DarkCard, RoundedCornerShape(Dimensions.cornerRadiusMedium)),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.paddingMedium),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextTertiary,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun GenderSegmentedControl(
    selectedGender: Gender?,
    onSelect: (Gender) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(Dimensions.cornerRadiusLarge))
            .padding(Dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingExtraSmall)
    ) {
        GenderSegmentItem(
            label = stringResource(R.string.gender_male),
            selected = selectedGender == Gender.MALE,
            onClick = { onSelect(Gender.MALE) },
            modifier = Modifier.weight(1f)
        )
        GenderSegmentItem(
            label = stringResource(R.string.gender_female),
            selected = selectedGender == Gender.FEMALE,
            onClick = { onSelect(Gender.FEMALE) },
            modifier = Modifier.weight(1f)
        )
        GenderSegmentItem(
            label = stringResource(R.string.gender_other),
            selected = selectedGender == Gender.OTHER,
            onClick = { onSelect(Gender.OTHER) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GenderSegmentItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) NeonGreen else Color.Transparent, shape)
            .border(
                border = BorderStroke(1.dp, if (selected) Color.Transparent else TextSecondary.copy(alpha = 0.25f)),
                shape = shape
            )
            .clickable { onClick() }
            .padding(vertical = Dimensions.paddingMedium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) DarkBackground else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GenderCard(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(Dimensions.cardHeightSmall)
            .background(
                if (isSelected) NeonGreen else DarkCard,
                RoundedCornerShape(Dimensions.cornerRadiusMedium)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingMedium)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isSelected) DarkBackground else TextSecondary,
                modifier = Modifier.size(Dimensions.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(Dimensions.paddingMedium))
            Text(
                text = when (gender) {
                    Gender.MALE -> stringResource(R.string.gender_male)
                    Gender.FEMALE -> stringResource(R.string.gender_female)
                    Gender.OTHER -> stringResource(R.string.gender_other)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) DarkBackground else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
} 