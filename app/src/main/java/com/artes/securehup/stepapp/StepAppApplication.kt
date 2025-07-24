package com.artes.securehup.stepapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StepAppApplication : Application() {
    // Uygulama başlatıldığında Hilt'in bağımlılık grafiğini oluşturmasını sağlar.
}