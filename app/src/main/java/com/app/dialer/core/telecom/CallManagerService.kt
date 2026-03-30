package com.app.dialer.core.telecom

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Foreground service stub for the persistent "Active Call" notification.
 *
 * Declared in AndroidManifest.xml with `foregroundServiceType="phoneCall"`.
 *
 * ### P1 scope
 * Scaffold stub — stops itself immediately on every start command. Full
 * notification management (ongoing call timer, missed-call, incoming-call) and
 * [CallEventBus] observation are implemented in Prompt 2 (InCallService module).
 */
class CallManagerService : Service() {

    companion object {
        private const val TAG = "CallManagerService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // P2+: post sticky "Active Call" foreground notification and start duration ticker
        Log.d(TAG, "onStartCommand — P1 stub")
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
