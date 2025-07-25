package com.artes.securehup.stepapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.artes.securehup.stepapp.service.StepTrackingService
import com.artes.securehup.stepapp.ui.navigation.AppNavigation
import com.artes.securehup.stepapp.ui.navigation.OnboardingNavigation
import com.artes.securehup.stepapp.ui.theme.StepappTheme
import com.artes.securehup.stepapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val activityRecognitionGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        val bodySensorsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            permissions[Manifest.permission.BODY_SENSORS] ?: false
        } else true
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        } else true
        
        android.util.Log.d("MainActivity", "Permissions - Activity: $activityRecognitionGranted, Body: $bodySensorsGranted, Notification: $notificationGranted")
        
        // En azından temel sensör izni varsa başlat
        if ((activityRecognitionGranted || bodySensorsGranted) && notificationGranted) {
            startStepTracking()
        } else {
            android.util.Log.w("MainActivity", "Required permissions not granted")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // İzinleri kontrol et ve step tracking'i başlat
        checkPermissionsAndStartTracking()
        
        setContent {
            StepappTheme {
                MainContent()
            }
        }
    }
    
    private fun checkPermissionsAndStartTracking() {
        val permissionsToRequest = mutableListOf<String>()
        
        // Activity Recognition izni (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
        
        // Body Sensors izni
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BODY_SENSORS)
            }
        }
        
        // Notification izni (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startStepTracking()
        }
    }
    
    private fun startStepTracking() {
        StepTrackingService.startService(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Service'i durdurmuyoruz çünkü arka planda çalışmaya devam etmeli
    }
}

@Composable
private fun MainContent(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        !uiState.isInitialized || uiState.isLoading -> {
            // Show loading screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        !uiState.isProfileCompleted -> {
            // Show onboarding
            OnboardingNavigation(
                onOnboardingComplete = {
                    viewModel.onOnboardingCompleted()
                }
            )
        }
        
        else -> {
            // Show main app
            AppNavigation()
        }
    }
    
    // Handle errors
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Here you can show a snackbar or dialog
            viewModel.clearError()
        }
    }
}
