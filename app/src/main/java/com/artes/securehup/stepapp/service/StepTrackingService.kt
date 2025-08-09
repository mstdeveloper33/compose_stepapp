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
import kotlinx.coroutines.cancel
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
    private var isUsingStepCounter = false

    // Accelerometer fallback için değişkenler
    private var lastAcceleration = 0f
    private var currentAcceleration = 0f
    private var accelerationThreshold = 15f
    private var stepCountFromAccelerometer = 0
    private var lastStepTime = 0L
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var midnightResetJob: kotlinx.coroutines.Job? = null
    private var dateCheckJob: kotlinx.coroutines.Job? = null
    private var currentDate: String = ""
    
    private fun restoreDailyData() {
        val prefs = getSharedPreferences("step_tracking", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val savedDate = prefs.getString("last_date", "")
        currentDate = today
        
        if (savedDate == today) {
            // Bugünkü verileri restore et
            initialStepCount = prefs.getLong("initial_step_count", 0L)
            stepCountFromAccelerometer = prefs.getInt("accelerometer_steps", 0)
            isInitialized = prefs.getBoolean("is_initialized", false)
            Log.d(TAG, "Restored daily data: initial=$initialStepCount, accel=$stepCountFromAccelerometer")
        } else {
            // Yeni gün başladı, verileri sıfırla
            resetDailyData()
            Log.d(TAG, "New day started, reset daily data")
        }
    }
    
    private fun resetDailyData() {
        val prefs = getSharedPreferences("step_tracking", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        
        // Verileri sıfırla
        initialStepCount = 0L
        currentStepCount = 0L
        dailyStepCount = 0
        stepCountFromAccelerometer = 0
        isInitialized = false
        currentDate = today
        
        // SharedPreferences'i güncelle
        prefs.edit()
            .putString("last_date", today)
            .putLong("initial_step_count", 0L)
            .putInt("accelerometer_steps", 0)
            .putBoolean("is_initialized", false)
            .apply()
        
        // Notification'ı force güncelle
        updateNotification()
        
        // Notification'ı async tekrar güncelle (cache clear için)
        serviceScope.launch {
            kotlinx.coroutines.delay(100)
            updateNotification()
            Log.d(TAG, "Notification force refreshed after reset")
        }
        
        Log.d(TAG, "Daily data reset completed for date: $today, notification updated")
    }
    
    private fun saveDailyData() {
        val prefs = getSharedPreferences("step_tracking", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        prefs.edit()
            .putString("last_date", today)
            .putLong("initial_step_count", initialStepCount)
            .putInt("accelerometer_steps", stepCountFromAccelerometer)
            .putBoolean("is_initialized", isInitialized)
            .apply()
    }
    
    private fun startMidnightResetTimer() {
        midnightResetJob?.cancel()
        
        midnightResetJob = serviceScope.launch {
            while (true) {
                val now = java.util.Calendar.getInstance()
                val midnight = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                
                val timeUntilMidnight = midnight.timeInMillis - now.timeInMillis
                Log.d(TAG, "Time until midnight: ${timeUntilMidnight / (1000 * 60)} minutes")
                
                try {
                    // Gece yarısına kadar bekle
                    kotlinx.coroutines.delay(timeUntilMidnight)
                    
                    // Gece yarısı geçti, verileri sıfırla
                    Log.d(TAG, "Midnight reached, resetting daily data")
                    resetDailyData()
                    
                    // Database'e yeni günün verisi olarak sıfır kaydet
                    // Yeni gün: delta'lar 0 geçilir
                    updateStepCount(0, 0L)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error in midnight reset timer", e)
                    break
                }
            }
        }
    }
    
    private fun checkDateChange() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        if (currentDate != today) {
            Log.d(TAG, "Date changed from $currentDate to $today")
            resetDailyData()
            
            // Timer'ları yeniden başlat
            startMidnightResetTimer()
            startPeriodicDateCheck()
        }
    }
    
    private fun startPeriodicDateCheck() {
        dateCheckJob?.cancel()
        
        dateCheckJob = serviceScope.launch {
            while (true) {
                try {
                    // Her 30 saniyede bir tarih kontrolü yap
                    kotlinx.coroutines.delay(30_000)
                    
                    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    if (currentDate != today) {
                        Log.d(TAG, "Periodic check: Date changed from $currentDate to $today")
                        resetDailyData()
                        
                        // Timer'ları yeniden başlat
                        startMidnightResetTimer()
                        break // Bu job'ı sonlandır, yeni başlatılacak
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error in periodic date check", e)
                    break
                }
            }
        }
        
        Log.d(TAG, "Periodic date check started (every 30 seconds)")
    }
    
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
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
        
        // Günlük verileri restore et
        restoreDailyData()
        
        // Gece yarısı reset timer'ını başlat
        startMidnightResetTimer()
        
        // Periodic date check timer'ını başlat (her 30 saniyede bir)
        startPeriodicDateCheck()
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called with action: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                startStepTracking()
            }
            ACTION_STOP_TRACKING -> {
                stopStepTracking()
            }
            null -> {
                // Service restart edildiğinde action null olabilir
                Log.d(TAG, "Service restarted, restarting step tracking")
                startStepTracking()
            }
        }
        return START_STICKY // Service kill edildiğinde otomatik restart
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "Task removed, but service should continue running")
        // Service'in devam etmesini sağla
        val restartIntent = Intent(this, StepTrackingService::class.java).apply {
            action = ACTION_START_TRACKING
        }
        startService(restartIntent)
    }

    private fun startStepTracking() {
        Log.d(TAG, "Starting step tracking")
        
        // Önce notification channel'ı oluştur
        createNotificationChannel()
        
        val notification = createNotification()
        try {
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Foreground service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service", e)
            return
        }
        
        // Mevcut sensörleri listele
        val availableSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "Available sensors count: ${availableSensors.size}")
        
        var sensorRegistered = false
        
                // Step Counter sensörünü kaydet (daha az batarya tüketir)
        stepCounterSensor?.let { sensor ->
            val success = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(TAG, "Step counter registration: $success")
            if (success) {
                isUsingStepCounter = true
                sensorRegistered = true
                Log.d(TAG, "Using step counter as primary sensor")
            }
        } ?: Log.w(TAG, "Step counter sensor not available")

        // Step Detector sensörünü kaydet (gerçek zamanlı)
        if (isUsingStepCounter) {
            stepDetectorSensor?.let { sensor ->
                val success = sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                Log.d(TAG, "Step detector registration: $success")
            } ?: Log.w(TAG, "Step detector sensor not available")
        }
        
        if (!sensorRegistered) {
            Log.w(TAG, "No step sensors available or registration failed on this device")
            // Fallback: Accelerometer kullanarak manual step detection
            startAccelerometerFallback()
        }
    }
    
    private fun startAccelerometerFallback() {
        Log.d(TAG, "Starting accelerometer fallback for step detection")
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        accelerometer?.let { sensor ->
            val success = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(TAG, "Accelerometer fallback registration: $success")
        } ?: Log.e(TAG, "No accelerometer available - step tracking not possible")
    }

    private fun stopStepTracking() {
        Log.d(TAG, "Stopping step tracking")
        sensorManager.unregisterListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
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
                Sensor.TYPE_ACCELEROMETER -> {
                    handleAccelerometer(sensorEvent.values)
                }
            }
        }
    }

    private var lastUpdateTimeMs: Long = 0L

    private fun handleStepCounter(totalSteps: Long) {
        // Önce tarih değişimi kontrolü yap
        checkDateChange()
        
        Log.d(TAG, "Step counter: $totalSteps")
        
        if (!isInitialized) {
            initialStepCount = totalSteps
            isInitialized = true
            saveDailyData()
            Log.d(TAG, "Initialized with step count: $initialStepCount")
        }
        
        val now = System.currentTimeMillis()
        if (lastUpdateTimeMs == 0L) lastUpdateTimeMs = now

        currentStepCount = totalSteps
        val newDaily = (currentStepCount - initialStepCount).toInt()
        val deltaSteps = (newDaily - dailyStepCount).coerceAtLeast(0)
        val deltaMillis = now - lastUpdateTimeMs
        dailyStepCount = newDaily
        lastUpdateTimeMs = now
        
        if (dailyStepCount >= 0) {
            updateStepCount(deltaSteps, deltaMillis)
            updateNotification()
            saveDailyData()
        }
    }

    private fun handleStepDetector() {
        // Step detector her adımda tetiklenir
        // Burada gerçek zamanlı feedback verilebilir
        Log.d(TAG, "Step detected")
    }
    
    private fun handleAccelerometer(values: FloatArray) {
        // Eğer step counter çalışıyorsa accelerometer'ı kullanma
        if (isUsingStepCounter) {
            return
        }
        
        val x = values[0]
        val y = values[1] 
        val z = values[2]
        
        // Toplam ivme hesapla
        lastAcceleration = currentAcceleration
        currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        
        val delta = currentAcceleration - lastAcceleration
        
        // Eşik değerini geçen ivme değişikliği algıla
        if (kotlin.math.abs(delta) > accelerationThreshold) {
            val currentTime = System.currentTimeMillis()
            
            // Son adımdan beri geçen süre kontrolü (300ms minimum)
            if (currentTime - lastStepTime > 300) {
                // Önce tarih değişimi kontrolü yap
                checkDateChange()
                
                stepCountFromAccelerometer++
                val deltaSteps = 1
                val deltaMillis = if (lastUpdateTimeMs == 0L) 0L else (currentTime - lastUpdateTimeMs)
                lastUpdateTimeMs = currentTime
                
                // Sadece accelerometer kullanıldığında güncellensin
                dailyStepCount = stepCountFromAccelerometer
                updateStepCount(deltaSteps, deltaMillis)
                updateNotification()
                saveDailyData()
                
                Log.d(TAG, "Step detected via accelerometer. Total: $dailyStepCount")
            }
        }
    }

    private fun updateStepCount(deltaSteps: Int, deltaMillis: Long) {
        serviceScope.launch {
            val result = updateStepsUseCase(dailyStepCount, deltaSteps, deltaMillis)
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
        
        // Önce notification'ı iptal et sonra yeniden göster (force refresh için)
        notificationManager.cancel(NOTIFICATION_ID)
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        Log.d(TAG, "Notification updated with steps: $dailyStepCount")
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d(TAG, "Creating notification with daily steps: $dailyStepCount")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Adım Takibi Aktif")
            .setContentText("Bugün: $dailyStepCount adım")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setAutoCancel(false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setWhen(System.currentTimeMillis()) // Timestamp ekle
            .setShowWhen(false) // Timestamp gösterme ama refresh tetikle
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
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
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
        midnightResetJob?.cancel()
        dateCheckJob?.cancel()
        // Tüm arka plan işleri sonlandır
        try {
            serviceScope.cancel()
        } catch (_: Exception) { }
    }
} 