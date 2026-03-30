package com.app.dialer.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Broadcast receiver that handles [Intent.ACTION_BOOT_COMPLETED].
 *
 * Declared in AndroidManifest.xml with RECEIVE_BOOT_COMPLETED permission.
 * Used in future prompts to reschedule alarms, restore notification channels,
 * or restart any persistent services after device reboot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d(TAG, "Boot completed — scheduling deferred work.")
            // Alarm rescheduling and notification channel restoration — implemented in Prompt N.
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
