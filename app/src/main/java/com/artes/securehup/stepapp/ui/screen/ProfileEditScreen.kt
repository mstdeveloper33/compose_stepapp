package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    
    // Initialize form with existing profile data
    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            age = it.age.toString()
            height = it.height.toString()
            weight = it.weight.toString()
            selectedGender = it.gender
        }
    }
    
    val isFormValid = name.isNotBlank() && 
                     age.isNotBlank() && 
                     height.isNotBlank() && 
                     weight.isNotBlank() && 
                     selectedGender != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Düzenle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Personal Information Section
            Text(
                text = "Kişisel Bilgiler",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("İsim") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) age = it },
                label = { Text("Yaş") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = height,
                    onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) height = it },
                    label = { Text("Boy (cm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = weight,
                    onValueChange = { if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) weight = it },
                    label = { Text("Kilo (kg)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Gender Selection
            Text(
                text = "Cinsiyet",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Gender.values().forEach { gender ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = selectedGender == gender,
                                onClick = { selectedGender = gender },
                                role = Role.RadioButton
                            ),
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
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            Button(
                onClick = {
                    if (isFormValid && profile != null) {
                        val updatedProfile = profile.copy(
                            name = name.trim(),
                            age = age.toInt(),
                            height = height.toDouble(),
                            weight = weight.toDouble(),
                            gender = selectedGender!!
                        )
                        viewModel.updateProfile(updatedProfile)
                    }
                },
                enabled = isFormValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Kaydet")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Handle success message
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Show success message and navigate back
            onNavigateBack()
            viewModel.clearMessages()
        }
    }
    
    // Handle error message
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Here you can show a snackbar
            viewModel.clearMessages()
        }
    }
} 