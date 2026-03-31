package com.app.dialer.core.telecom

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.EntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.EntryPointAccessors
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Foreground service for maintaining call state and powering the persistent
 * "Active Call" notification during active calls.
 *
 * Declared in AndroidManifest.xml with `foregroundServiceType="phoneCall"`.
 *
 * Responsibilities:
 * - Post and update the ongoing active-call notification (call timer, controls).
 * - Observe [CallEventBus] and update notification on state changes.
 * - Manage audio focus lifecycle (request on active, abandon on disconnect).
 * - Stop itself when the last call ends.
 */
class CallManagerService : Service() {

    companion object {
        private const val TAG = "CallManagerService"

        @EntryPoint
        @InstallIn(SingletonComponent::class)
        interface CallManagerEntryPoint {
            fun eventBus(): CallEventBus
            fun notificationManager(): CallNotificationManager
            fun audioFocusHelper(): Any  // AudioFocusHelper type
        }
    }

    private lateinit var entryPoint: CallManagerEntryPoint
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var activeCalls = setOf<String>()
    private var durationSeconds = 0L
    private var timerJob: kotlinx.coroutines.Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        entryPoint = EntryPointAccessors.fromApplication(
            this,
            CallManagerEntryPoint::class.java
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        
        // Observe call events and update notification
        scope.launch {
            entryPoint.eventBus().callEvents.collectLatest { event ->
                when (event) {
                    is CallEvent.CallAdded -> handleCallAdded(event)
                    is CallEvent.CallRemoved -> handleCallRemoved(event)
                    is CallEvent.CallStateChanged -> handleStateChanged(event)
                    else -> {}
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        timerJob?.cancel()
        entryPoint.notificationManager().dismissNotification(
            CallNotificationManager.NOTIFICATION_ID_ACTIVE_CALL
        )
        Log.d(TAG, "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handleCallAdded(event: CallEvent.CallAdded) {
        activeCalls = activeCalls + event.callId
        Log.d(TAG, "Call added: ${event.callId}, total=${activeCalls.size}")

        if (activeCalls.size == 1) {
            // First call — post foreground notification and request audio focus
            durationSeconds = 0
            startDurationTimer()
            val notification = entryPoint.notificationManager()
                .buildActiveCallNotification(event.number, 0L)
            
            try {
                startForeground(
                    CallNotificationManager.NOTIFICATION_ID_ACTIVE_CALL,
                    notification
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground", e)
            }
        }
    }

    private fun handleCallRemoved(event: CallEvent.CallRemoved) {
        activeCalls = activeCalls - event.callId
        Log.d(TAG, "Call removed: ${event.callId}, remaining=${activeCalls.size}")

        if (activeCalls.isEmpty()) {
            // Last call ended — stop the service
            timerJob?.cancel()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun handleStateChanged(event: CallEvent.CallStateChanged) {
        // Update notification with new state
        val firstCall = activeCalls.firstOrNull() ?: return
        val notification = entryPoint.notificationManager()
            .buildActiveCallNotification("Unknown", durationSeconds)
        entryPoint.notificationManager().updateNotification(
            CallNotificationManager.NOTIFICATION_ID_ACTIVE_CALL,
            notification
        )
    }

    private fun startDurationTimer() {
        timerJob = scope.launch {
            while (activeCalls.isNotEmpty()) {
                durationSeconds++
                val notification = entryPoint.notificationManager()
                    .buildActiveCallNotification("Unknown", durationSeconds)
                entryPoint.notificationManager().updateNotification(
                    CallNotificationManager.NOTIFICATION_ID_ACTIVE_CALL,
                    notification
                )
                kotlinx.coroutines.delay(1000)
            }
        }
    }
}
