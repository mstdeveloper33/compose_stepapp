package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.ui.theme.*

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
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(Dimensions.paddingLarge)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Dimensions.paddingXXLarge))
        
        // Header
        Text(
            text = "Kişisel Bilgileriniz",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Size özel hedefler belirleyebilmemiz için birkaç bilgiye ihtiyacımız var",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimensions.paddingSmall,
                    bottom = Dimensions.paddingExtraLarge
                )
        )
        
        // Progress Bar
        LinearProgressIndicator(
            progress = { 0.33f },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.progressBarHeight)
                .clip(RoundedCornerShape(Dimensions.progressBarHeight / 2)),
            color = NeonGreen,
            trackColor = DarkCard,
        )
        
        Spacer(modifier = Modifier.height(Dimensions.paddingXXLarge))
        
        // Name Field
        CustomInputField(
            value = name,
            onValueChange = { name = it },
            label = "İsminiz",
            placeholder = "Adınızı girin"
        )
        
        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
        
        // Age and Height Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall + Dimensions.paddingExtraSmall)
        ) {
            CustomInputField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) age = it },
                label = "Yaş",
                placeholder = "25",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            
            CustomInputField(
                value = height,
                onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) height = it },
                label = "Boy (cm)",
                placeholder = "175",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
        
        // Weight Field
        CustomInputField(
            value = weight,
            onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) weight = it },
            label = "Kilo (kg)",
            placeholder = "70"
        )
        
        Spacer(modifier = Modifier.height(Dimensions.paddingExtraLarge))
        
        // Gender Selection
        Text(
            text = "Cinsiyet",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = Dimensions.paddingMedium)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall + Dimensions.paddingExtraSmall)
        ) {
            Gender.values().forEach { gender ->
                GenderCard(
                    gender = gender,
                    isSelected = selectedGender == gender,
                    onClick = { selectedGender = gender }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Continue Button
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
                    fontSize = 18.sp,
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
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = if (isFormValid) DarkBackground else TextTertiary,
                        modifier = Modifier.size(Dimensions.iconSizeMedium - Dimensions.paddingExtraSmall)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
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
private fun GenderCard(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.cardHeightSmall)
            .background(
                if (isSelected) NeonGreen else DarkCard,
                RoundedCornerShape(Dimensions.cornerRadiusMedium)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isSelected) DarkBackground else TextSecondary,
                modifier = Modifier.size(Dimensions.iconSizeMedium)
            )
            
            Spacer(modifier = Modifier.width(Dimensions.paddingMedium))
            
            Text(
                text = when(gender) {
                    Gender.MALE -> "Erkek"
                    Gender.FEMALE -> "Kadın"
                    Gender.OTHER -> "Diğer"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) DarkBackground else TextPrimary
            )
        }
    }
} 