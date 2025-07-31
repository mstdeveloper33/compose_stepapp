package com.artes.securehup.stepapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Ana Sayfa",
        icon = Icons.Default.Home
    )

    object Stats : BottomNavItem(
        route = "stats/0",
        title = "İstatistikler",
        icon = Icons.Default.DateRange
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profil",
        icon = Icons.Default.Person
    )
}

@Composable
fun HealthBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                selected = when {
                    item == BottomNavItem.Home -> currentRoute == item.route
                    item == BottomNavItem.Stats -> currentRoute?.startsWith("stats/") == true
                    else -> currentRoute == item.route
                },
                onClick = {
                    onNavigate(item.route)
                }
            )
        }
    }
} 