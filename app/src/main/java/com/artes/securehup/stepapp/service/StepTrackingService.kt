package com.artes.securehup.stepapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.artes.securehup.stepapp.MainActivity
import com.artes.securehup.stepapp.domain.usecase.UpdateStepsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepTrackingService : Service(), SensorEventListener {

    @Inject
    lateinit var updateStepsUseCase: UpdateStepsUseCase

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    
    private var initialStepCount = 0L
    private var currentStepCount = 0L
    private var dailyStepCount = 0
    private var isInitialized = false
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "step_tracking_channel"
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
        
        private const val TAG = "StepTrackingService"
        
        fun startService(context: Context) {
            val intent = Intent(context, StepTrackingService::class.java).apply {
                action = ACTION_START_TRACKING
            }
            context.startForegroundService(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, StepTrackingService::class.java).apply {
                action = ACTION_STOP_TRACKING
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "StepTrackingService created")
        
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                startStepTracking()
            }
            ACTION_STOP_TRACKING -> {
                stopStepTracking()
            }
        }
        return START_STICKY
    }

    private fun startStepTracking() {
        Log.d(TAG, "Starting step tracking")
        
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // Step Counter sensörünü kaydet (daha az batarya tüketir)
        stepCounterSensor?.let { sensor ->
            val success = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
            Log.d(TAG, "Step counter registration: $success")
        }
        
        // Step Detector sensörünü kaydet (gerçek zamanlı)
        stepDetectorSensor?.let { sensor ->
            val success = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
            Log.d(TAG, "Step detector registration: $success")
        }
        
        if (stepCounterSensor == null && stepDetectorSensor == null) {
            Log.w(TAG, "No step sensors available on this device")
            // Manuel step counting veya alternatif yöntemler burada eklenebilir
        }
    }

    private fun stopStepTracking() {
        Log.d(TAG, "Stopping step tracking")
        sensorManager.unregisterListener(this)
        stopForeground(true)
        stopSelf()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    handleStepCounter(sensorEvent.values[0].toLong())
                }
                Sensor.TYPE_STEP_DETECTOR -> {
                    handleStepDetector()
                }
            }
        }
    }

    private fun handleStepCounter(totalSteps: Long) {
        Log.d(TAG, "Step counter: $totalSteps")
        
        if (!isInitialized) {
            initialStepCount = totalSteps
            isInitialized = true
            Log.d(TAG, "Initialized with step count: $initialStepCount")
        }
        
        currentStepCount = totalSteps
        dailyStepCount = (currentStepCount - initialStepCount).toInt()
        
        if (dailyStepCount >= 0) {
            updateStepCount()
            updateNotification()
        }
    }

    private fun handleStepDetector() {
        // Step detector her adımda tetiklenir
        // Burada gerçek zamanlı feedback verilebilir
        Log.d(TAG, "Step detected")
    }

    private fun updateStepCount() {
        serviceScope.launch {
            val result = updateStepsUseCase(dailyStepCount)
            if (result.isSuccess) {
                Log.d(TAG, "Updated daily steps: $dailyStepCount")
            } else {
                Log.e(TAG, "Failed to update steps", result.exceptionOrNull())
            }
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Adım Takibi Aktif")
            .setContentText("Bugün: $dailyStepCount adım")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Adım Takibi",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Arka planda adım sayısını takip eder"
                setSound(null, null)
                enableVibration(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: ${sensor?.name}, accuracy: $accuracy")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "StepTrackingService destroyed")
        sensorManager.unregisterListener(this)
    }
} 