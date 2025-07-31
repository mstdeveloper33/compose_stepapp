package com.artes.securehup.stepapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.artes.securehup.stepapp.ui.screen.HomeScreen
import com.artes.securehup.stepapp.ui.screen.StatsScreen
import com.artes.securehup.stepapp.ui.screen.ProfileScreen
import com.artes.securehup.stepapp.ui.screen.ProfileEditScreen
import com.artes.securehup.stepapp.ui.screen.GoalsEditScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            HealthBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        BottomNavItem.Home.route -> {
                            // Anasayfaya giderken stats sayfasından çık
                            if (currentRoute?.startsWith("stats/") == true) {
                                navController.navigate(route) {
                                    popUpTo("stats/0") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        BottomNavItem.Stats.route -> {
                            // Stats sayfasına git
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        else -> {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToStats = { selectedTab ->
                        navController.navigate("stats/$selectedTab") {
                            // Stats sayfasına giderken anasayfayı back stack'ten çıkar
                            popUpTo(BottomNavItem.Home.route) {
                                saveState = true
                            }
                            // Yeni bir instance oluştur
                            launchSingleTop = true
                            // State'i geri yükle
                            restoreState = true
                        }
                    }
                )
            }
            
            composable(
                route = "stats/{selectedTab}",
                arguments = listOf(
                    navArgument("selectedTab") {
                        type = androidx.navigation.NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val selectedTab = backStackEntry.arguments?.getInt("selectedTab") ?: 0
                StatsScreen(
                    initialSelectedTab = selectedTab,
                    onNavigateBack = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo("stats/$selectedTab") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            composable(BottomNavItem.Stats.route) {
                StatsScreen(
                    initialSelectedTab = 0,
                    onNavigateBack = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(BottomNavItem.Stats.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToEdit = {
                        navController.navigate("profile_edit")
                    },
                    onNavigateToGoalsEdit = {
                        navController.navigate("goals_edit")
                    }
                )
            }
            
            composable("profile_edit") {
                ProfileEditScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable("goals_edit") {
                GoalsEditScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
} 