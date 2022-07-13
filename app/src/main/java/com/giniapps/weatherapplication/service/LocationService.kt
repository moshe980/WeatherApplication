package com.giniapps.weatherapplication.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.giniapps.mylocation4.notification.NotificationGenerator
import com.giniapps.weatherapplication.MainActivity
import com.giniapps.weatherapplication.repository.WeatherRepository
import com.giniapps.weatherapplication.utils.toText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : LifecycleService() {
    private var configurationChange = false
    private var serviceRunningInForeground = false
    private val localBinder = LocalBinder()
    private lateinit var notificationManager: NotificationManager
    private var currentLocation: Location? = null

    @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var notificationGenerator: NotificationGenerator

    private var locationFlow: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        val cancelLocationTrackingFromNotification =
            intent?.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)
                ?: false

        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }
        return super.onStartCommand(intent, flags, START_NOT_STICKY)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d(TAG, "onBind()")
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")
        if (!configurationChange) {
            Log.d(TAG, "Start foreground service")
            val notification = notificationGenerator.getNotificationCompatBuilder(
                NOTIFICATION_ID,
                "location",
                currentLocation.toText(),
                true,
                Intent(this, MainActivity::class.java)
            ).build()
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    @SuppressLint("MissingPermission")
    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")
        startService(Intent(applicationContext, LocationService::class.java))

        locationFlow = lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getLocations()
                    .onEach {
                        Log.d(TAG, "Service location: ${it.toText()}")
                        currentLocation = it
                    }.collect {
                        val intent=Intent(applicationContext,MainActivity::class.java)
                        if (serviceRunningInForeground) {
                            notificationManager.notify(
                                NOTIFICATION_ID,
                                notificationGenerator.getNotificationCompatBuilder(
                                    NOTIFICATION_ID,
                                    "location",
                                    currentLocation.toText(),
                                    true,
                                    intent
                                ).build()
                            )
                        }
                    }
            }
        }
    }

    fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        locationFlow?.cancel()
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }


    companion object {
        private const val TAG = "ForegroundOnlyLocationService"

        private const val PACKAGE_NAME = "com.giniapps.mylocation4"


        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678
    }

}
