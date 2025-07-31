package com.artes.securehup.stepapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.ui.viewmodel.StatsViewModel
import com.artes.stepapp.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

private val CardBg = Color(0xFF181818)
private val StepColor = Color(0xFFB6E94B)
private val CalorieColor = Color(0xFFFFA940)
private val DistanceColor = Color(0xFF5B9EFF)
private val ActiveColor = Color(0xFFA259FF)

@Composable
fun StatsScreen(
    initialSelectedTab: Int = 0,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(initialSelectedTab) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().time) }
    var graphData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    // Grafik verilerini yükle
    LaunchedEffect(selectedTab) {
        graphData = viewModel.getLast7DaysData(selectedTab)
    }

    val tabs = listOf(
        Triple("Adımlar", R.drawable.step, StepColor),
        Triple("Kaloriler", R.drawable.fire, CalorieColor),
        Triple("Mesafe", R.drawable.km, DistanceColor),
        Triple("Aktif Süre", R.drawable.clock, ActiveColor)
    )

    // Seçilen tarihe göre verileri al
    val selectedDateString =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
    val todayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Seçilen tarih bugünse bugünün verilerini, değilse seçilen tarihin verilerini kullan
    val isToday = selectedDateString == todayString
    val displayData = if (isToday) {
        uiState.todayHealthData
    } else {
        // Burada seçilen tarihin verilerini almak için viewModel'e bir fonksiyon eklenebilir
        // Şimdilik bugünün verilerini kullanıyoruz
        uiState.todayHealthData
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CardBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Geri Butonu
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = tabs[selectedTab].first,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Tab Dropdown
                var expanded by remember { mutableStateOf(false) }

                Box {
                    Box(
                        modifier = Modifier
                            .background(
                                tabs[selectedTab].third.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { expanded = true }
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = tabs[selectedTab].second),
                            contentDescription = null,
                            tint = tabs[selectedTab].third,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBg, RoundedCornerShape(12.dp))
                    ) {
                        tabs.forEachIndexed { index, (title, icon, color) ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = icon),
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = title,
                                            color = Color.White
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTab = index
                                    expanded = false
                                },
                                modifier = Modifier.background(
                                    if (selectedTab == index) color.copy(alpha = 0.2f) else Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }

        // Date Selector
        item {
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                activeColor = tabs[selectedTab].third
            )
        }

        // Daily Summary
        item {
            DailySummaryCard(
                title = tabs[selectedTab].first,
                icon = tabs[selectedTab].second,
                color = tabs[selectedTab].third,
                value = when (selectedTab) {
                    0 -> displayData?.steps ?: 0
                    1 -> displayData?.calories ?: 0
                    2 -> (displayData?.distance?.toInt() ?: 0)
                    else -> (displayData?.activeTime?.toInt() ?: 0)
                },
                goal = when (selectedTab) {
                    0 -> uiState.userProfile?.dailyStepGoal ?: 10000
                    1 -> uiState.userProfile?.dailyCalorieGoal ?: 500
                    2 -> 7
                    else -> uiState.userProfile?.dailyActiveTimeGoal?.toInt() ?: 60
                },
                unit = when (selectedTab) {
                    0 -> "adım"
                    1 -> "kcal"
                    2 -> "km"
                    else -> "dk"
                },
                selectedDate = selectedDate
            )
        }

        // Trend Graph
        item {
            TrendGraphCard(
                title = "${tabs[selectedTab].first} Trendi",
                color = tabs[selectedTab].third,
                data = graphData,
                maxValue = when (selectedTab) {
                    0 -> uiState.userProfile?.dailyStepGoal ?: 10000
                    1 -> uiState.userProfile?.dailyCalorieGoal ?: 500
                    2 -> 7
                    else -> uiState.userProfile?.dailyActiveTimeGoal?.toInt() ?: 60
                }
            )
        }

        // Alt kısımda biraz boşluk bırak (bottom navigation için)
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DateSelector(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    activeColor: Color
) {
    val dateFormat = SimpleDateFormat("E", Locale("tr", "TR"))
    val dayFormat = SimpleDateFormat("d", Locale("tr", "TR"))

    // Son günden itibaren geriye dönük 14 gün oluştur
    val dates = (0..13).map { dayOffset ->
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -dayOffset)
        }.time
    }.reversed() // En son günü en sağda göstermek için ters çevir

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.3f))
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            items(dates) { date ->
                val isSelected = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) ==
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                val dayName = dateFormat.format(date).take(2)
                val dayNumber = dayFormat.format(date)
                val isToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) ==
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                Card(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = when {
                                isSelected -> activeColor
                                isToday -> activeColor.copy(alpha = 0.6f)
                                else -> activeColor.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onDateSelected(date) },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isSelected -> activeColor.copy(alpha = 0.15f)
                            isToday && !isSelected -> activeColor.copy(alpha = 0.08f)
                            else -> CardBg
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = dayName,
                            fontSize = 12.sp,
                            color = when {
                                isSelected -> activeColor
                                isToday -> activeColor.copy(alpha = 0.8f)
                                else -> Color.White.copy(alpha = 0.7f)
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = dayNumber,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSelected -> activeColor
                                isToday -> activeColor.copy(alpha = 0.8f)
                                else -> Color.White
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Seçili gün için dolu daire, bugün için yarı şeffaf, diğerleri için çok açık
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    when {
                                        isSelected -> activeColor
                                        isToday -> activeColor.copy(alpha = 0.6f)
                                        else -> activeColor.copy(alpha = 0.2f)
                                    },
                                    CircleShape
                                )
                        )

                        // Seçili gün için ek görsel ipucu
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(2.dp)
                                    .background(
                                        activeColor,
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailySummaryCard(
    title: String,
    icon: Int,
    color: Color,
    value: Int,
    goal: Int,
    unit: String,
    selectedDate: Date
) {
    val percentage = (value.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val percentageText = "${(percentage * 100).toInt()}%"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = SimpleDateFormat("d MMMM yyyy", Locale("tr", "TR")).format(selectedDate),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$value $unit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hedefinizin $percentageText'i",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (title) {
                    "Adımlar" -> "Adımlarını artırarak kendi rekorunu kır!"
                    "Kaloriler" -> "Yakılan her kalori seni hedefe yaklaştırıyor!"
                    "Mesafe" -> "Her kilometre seni daha güçlü yapıyor!"
                    else -> "Aktif kalmak sağlığın anahtarı!"
                },
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun TrendGraphCard(
    title: String,
    color: Color,
    data: List<Pair<String, Int>>,
    maxValue: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Y-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val step = maxValue / 3

                (0..3).forEach { i ->
                    Text(
                        text = (maxValue - (i * step)).toString(),
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Graph area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Grid lines
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                        )
                    }
                }

                // Area chart
                if (data.isNotEmpty()) {
                    val points = data.mapIndexed { index, (_, value) ->
                        val x = (index.toFloat() / (data.size - 1)) * 100
                        val y = 100 - (value.toFloat() / maxValue * 100)
                        Offset(x, y)
                    }

                    // Draw area chart
                    if (points.size > 1) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val path = Path()
                        path.moveTo(points.first().x * size.width / 100, size.height)
                        path.lineTo(
                            points.first().x * size.width / 100,
                            points.first().y * size.height / 100
                        )

                        points.drop(1).forEach { point ->
                            path.lineTo(point.x * size.width / 100, point.y * size.height / 100)
                        }

                        path.lineTo(points.last().x * size.width / 100, size.height)
                        path.close()

                        // Draw gradient fill
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    color.copy(alpha = 0.3f),
                                    color.copy(alpha = 0.1f)
                                )
                            )
                        )

                        // Draw line
                        val linePath = Path()
                        linePath.moveTo(
                            points.first().x * size.width / 100,
                            points.first().y * size.height / 100
                        )

                        points.drop(1).forEach { point ->
                            linePath.lineTo(point.x * size.width / 100, point.y * size.height / 100)
                        }

                        drawPath(
                            path = linePath,
                            color = color,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Draw data points
                        points.forEach { point ->
                            drawCircle(
                                color = color,
                                radius = 4.dp.toPx(),
                                center = Offset(
                                    point.x * size.width / 100,
                                    point.y * size.height / 100
                                )
                            )
                        }
                    }
                    }
                } else {
                    // Veri yoksa mesaj göster
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz veri yok",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Data labels
                if (data.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        data.forEach { (label, value) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = value.toString(),
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


