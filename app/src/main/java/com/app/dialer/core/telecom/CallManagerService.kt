package com.app.dialer.core.telecom

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Foreground service responsible for the persistent "Active Call" notification.
 *
 * Started by [InCallServiceImpl] when the first call is added and stopped when
 * the last call is removed.
 *
 * ### Foreground service type
 * Declared with `android:foregroundServiceType="phoneCall"` in the manifest so
 * the 3-argument [startForeground] overload on API 29+ can provide the type.
 *
 * ### Hilt access
 * Uses a manual [EntryPoint] because Hilt cannot inject into arbitrary [Service]
 * subclasses without `@AndroidEntryPoint`. Using `@AndroidEntryPoint` here would
 * be fine, but the entry-point pattern keeps Telecom services consistent.
 */
class CallManagerService : Service() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ServiceEntryPoint {
        fun callEventBus(): CallEventBus
        fun callNotificationManager(): CallNotificationManager
    }

    companion object {
        private const val TAG = "CallManagerService"
        private const val FOREGROUND_NOTIFICATION_ID =
            CallNotificationManager.NOTIFICATION_ID_ACTIVE_CALL
    }

    private lateinit var callEventBus: CallEventBus
    private lateinit var notificationManager: CallNotificationManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Tracks elapsed seconds since the most recent call became ACTIVE. */
    private var callStartEpochMs: Long = 0L
    private var durationTickJob: Job? = null
    private var activeCallNumber: String = ""

    // ── Service lifecycle ────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        val ep = EntryPointAccessors.fromApplication(
            applicationContext, ServiceEntryPoint::class.java
        )
        callEventBus = ep.callEventBus()
        notificationManager = ep.callNotificationManager()
        notificationManager.createNotificationChannels()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        val initialNotification = notificationManager.buildActiveCallNotification(
            number = activeCallNumber.ifBlank { "Connecting…" },
            durationSeconds = 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                FOREGROUND_NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, initialNotification)
        }

        observeCallEvents()

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        durationTickJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ── Event observation ────────────────────────────────────────────────────

    private fun observeCallEvents() {
        scope.launch {
            callEventBus.callEvents.collect { event ->
                when (event) {
                    is CallEvent.CallAdded -> {
                        activeCallNumber = event.number
                        updateNotification()
                    }

                    is CallEvent.CallStateChanged -> {
                        when (event.state) {
                            CallState.ACTIVE -> {
                                callStartEpochMs = System.currentTimeMillis()
                                startDurationTicker()
                            }
                            CallState.DISCONNECTED, CallState.DISCONNECTING -> {
                                durationTickJob?.cancel()
                            }
                            else -> Unit
                        }
                    }

                    is CallEvent.CallRemoved -> {
                        durationTickJob?.cancel()
                        // InCallServiceImpl calls stopService when no calls remain
                    }

                    else -> Unit
                }
            }
        }
    }

    // ── Duration ticker ──────────────────────────────────────────────────────

    private fun startDurationTicker() {
        durationTickJob?.cancel()
        durationTickJob = scope.launch {
            while (true) {
                delay(1_000)
                val elapsedSeconds = (System.currentTimeMillis() - callStartEpochMs) / 1_000
                updateNotification(elapsedSeconds)
            }
        }
    }

    private fun updateNotification(durationSeconds: Long = 0) {
        val notification = notificationManager.buildActiveCallNotification(
            number = activeCallNumber.ifBlank { "In Call" },
            durationSeconds = durationSeconds
        )
        notificationManager.updateNotification(FOREGROUND_NOTIFICATION_ID, notification)
    }
}
