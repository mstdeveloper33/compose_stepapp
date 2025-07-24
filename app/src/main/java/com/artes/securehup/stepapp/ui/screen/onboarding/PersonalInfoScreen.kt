package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artes.securehup.stepapp.domain.model.Gender

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Kişisel Bilgileriniz",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Size özel hedefler belirleyebilmemiz için birkaç bilgiye ihtiyacımız var.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        
        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("İsminiz") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Age Field
        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) age = it },
            label = { Text("Yaşınız") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            supportingText = { Text("18-99 arası") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Height Field
        OutlinedTextField(
            value = height,
            onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) height = it },
            label = { Text("Boyunuz (cm)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            supportingText = { Text("Örnek: 175") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Weight Field
        OutlinedTextField(
            value = weight,
            onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) weight = it },
            label = { Text("Kilonuz (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            supportingText = { Text("Örnek: 70") }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Gender Selection
        Text(
            text = "Cinsiyet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Gender.values().forEach { gender ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = null
                    )
                    Text(
                        text = when(gender) {
                            Gender.MALE -> "Erkek"
                            Gender.FEMALE -> "Kadın"
                            Gender.OTHER -> "Diğer"
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Geri")
            }
            
            Button(
                onClick = {
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
                enabled = isFormValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Devam")
            }
        }
    }
} 