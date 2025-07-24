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
                    navController.navigate(route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
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
                HomeScreen()
            }
            
            composable(BottomNavItem.Stats.route) {
                StatsScreen()
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