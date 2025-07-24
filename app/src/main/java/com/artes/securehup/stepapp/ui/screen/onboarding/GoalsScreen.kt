package com.artes.securehup.stepapp.ui.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onComplete: (Int, Int, Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stepGoal by remember { mutableStateOf("10000") }
    var calorieGoal by remember { mutableStateOf("2000") }
    var activeTimeGoal by remember { mutableStateOf("60") }
    
    val isFormValid = stepGoal.isNotBlank() && 
                     calorieGoal.isNotBlank() && 
                     activeTimeGoal.isNotBlank()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Günlük Hedefleriniz",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Size uygun günlük hedefler belirleyin. Bu hedefleri daha sonra değiştirebilirsiniz.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        
        // Goals Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Step Goal
            GoalCard(
                title = "Günlük Adım Hedefi",
                description = "Günde kaç adım atmak istiyorsunuz?",
                emoji = "👟",
                value = stepGoal,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 6) stepGoal = it },
                suffix = "adım",
                recommendation = "Önerilen: 8,000-12,000"
            )
            
            // Calorie Goal
            GoalCard(
                title = "Günlük Kalori Hedefi",
                description = "Günde kaç kalori yakmayı hedefliyorsunuz?",
                emoji = "🔥",
                value = calorieGoal,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 5) calorieGoal = it },
                suffix = "kalori",
                recommendation = "Önerilen: 1,800-2,500"
            )
            
            // Active Time Goal
            GoalCard(
                title = "Günlük Aktif Süre",
                description = "Günde kaç dakika aktif olmak istiyorsunuz?",
                emoji = "⏱️",
                value = activeTimeGoal,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) activeTimeGoal = it },
                suffix = "dakika",
                recommendation = "Önerilen: 30-90"
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 İpucu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Hedeflerinizi gerçekçi belirleyin ve zamanla artırın. Başarı sürekli küçük adımlarla gelir!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                )
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
                        onComplete(
                            stepGoal.toInt(),
                            calorieGoal.toInt(),
                            activeTimeGoal.toLong()
                        )
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Tamamla")
            }
        }
    }
}

@Composable
private fun GoalCard(
    title: String,
    description: String,
    emoji: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    recommendation: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text(suffix) },
                singleLine = true
            )
            
            Text(
                text = recommendation,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.End
            )
        }
    }
} 