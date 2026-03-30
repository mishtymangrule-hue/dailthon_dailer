package com.app.dialer.core.telecom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.dialer.MainActivity
import com.app.dialer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central manager for all call-related system notifications.
 *
 * ### P2+ scope
 * This class is a Prompt-2 (InCallService module) concern. Notification channel
 * creation is idempotent and safe to call early, but notification posting is not
 * triggered by [InCallServiceImpl] in P1.
 *
 * Owns three notification channels:
 * - [CHANNEL_ACTIVE_CALL]: ongoing in-call notification with call controls.
 * - [CHANNEL_INCOMING_CALL]: high-priority full-screen incoming call alert.
 * - [CHANNEL_MISSED_CALL]: missed-call summary with callback action.
 */
@Singleton
class CallNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    companion object {
        // ── Channel IDs ──────────────────────────────────────────────────────
        const val CHANNEL_ACTIVE_CALL = "active_call"
        const val CHANNEL_INCOMING_CALL = "incoming_call"
        const val CHANNEL_MISSED_CALL = "missed_call"

        // ── Notification IDs ─────────────────────────────────────────────────
        const val NOTIFICATION_ID_ACTIVE_CALL = 1001
        const val NOTIFICATION_ID_INCOMING_CALL = 1002
        const val NOTIFICATION_ID_MISSED_CALL = 1003
    }

    // ─── Channel setup ────────────────────────────────────────────────────────

    /**
     * Creates all three notification channels. Safe to call multiple times —
     * no-op if channels already exist.
     */
    fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_ACTIVE_CALL,
                "Active Call",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows controls for an ongoing call"
                setSound(null, null)         // Call audio handles sound — not the notification
                enableVibration(false)
            },
            NotificationChannel(
                CHANNEL_INCOMING_CALL,
                "Incoming Call",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for incoming calls with answer/decline buttons"
            },
            NotificationChannel(
                CHANNEL_MISSED_CALL,
                "Missed Calls",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Missed call summaries with callback shortcut"
            }
        )

        val systemNm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { systemNm.createNotificationChannel(it) }
    }

    // ─── Notification builders ────────────────────────────────────────────────

    /**
     * Builds (but does not post) the ongoing active-call notification.
     * Called by [CallManagerService.onStartCommand] to pass to [startForeground].
     */
    fun buildActiveCallNotification(number: String, durationSeconds: Long): Notification {
        val endCallIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, CallBroadcastReceiver::class.java).apply {
                action = CallBroadcastReceiver.ACTION_END_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val speakerIntent = PendingIntent.getBroadcast(
            context, 1,
            Intent(context, CallBroadcastReceiver::class.java).apply {
                action = CallBroadcastReceiver.ACTION_TOGGLE_SPEAKER
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val muteIntent = PendingIntent.getBroadcast(
            context, 2,
            Intent(context, CallBroadcastReceiver::class.java).apply {
                action = CallBroadcastReceiver.ACTION_TOGGLE_MUTE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val openUiIntent = PendingIntent.getActivity(
            context, 10,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val durationText = formatDuration(durationSeconds)
        val contentText = if (durationSeconds > 0) "$number · $durationText" else number

        return NotificationCompat.Builder(context, CHANNEL_ACTIVE_CALL)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Active Call")
            .setContentText(contentText)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(openUiIntent)
            .addAction(android.R.drawable.ic_lock_silent_mode_off, "Speaker", speakerIntent)
            .addAction(android.R.drawable.ic_lock_silent_mode, "Mute", muteIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "End", endCallIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    /** Builds and posts the active-call notification (for updates after [startForeground]). */
    fun showActiveCallNotification(number: String, durationSeconds: Long) {
        val notification = buildActiveCallNotification(number, durationSeconds)
        notificationManager.notify(NOTIFICATION_ID_ACTIVE_CALL, notification)
    }

    /**
     * Shows a high-priority full-screen incoming-call notification.
     *
     * On API 29+ and when [android.Manifest.permission.USE_FULL_SCREEN_INTENT] is held,
     * the system will display a heads-up or full-screen UI.
     */
    fun showIncomingCallNotification(number: String, contactName: String?) {
        val displayName = contactName?.takeIf { it.isNotBlank() } ?: number

        val fullScreenIntent = PendingIntent.getActivity(
            context, 20,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                action = "com.app.dialer.action.INCOMING_CALL"
                putExtra(CallBroadcastReceiver.EXTRA_PHONE_NUMBER, number)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val answerIntent = PendingIntent.getBroadcast(
            context, 21,
            Intent(context, CallBroadcastReceiver::class.java).apply {
                action = CallBroadcastReceiver.ACTION_ANSWER_CALL
                putExtra(CallBroadcastReceiver.EXTRA_PHONE_NUMBER, number)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val declineIntent = PendingIntent.getBroadcast(
            context, 22,
            Intent(context, CallBroadcastReceiver::class.java).apply {
                action = CallBroadcastReceiver.ACTION_REJECT_CALL
                putExtra(CallBroadcastReceiver.EXTRA_PHONE_NUMBER, number)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_INCOMING_CALL)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Incoming Call")
            .setContentText(displayName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenIntent, true)
            .addAction(android.R.drawable.ic_menu_call, "Answer", answerIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", declineIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(NOTIFICATION_ID_INCOMING_CALL, notification)
    }

    /**
     * Shows a missed-call notification with a tap-to-callback action.
     */
    fun showMissedCallNotification(number: String, contactName: String?) {
        val displayName = contactName?.takeIf { it.isNotBlank() } ?: number

        val callBackIntent = PendingIntent.getActivity(
            context, 30,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                action = Intent.ACTION_DIAL
                data = android.net.Uri.parse("tel:$number")
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MISSED_CALL)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Missed Call")
            .setContentText("Missed call from $displayName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MISSED_CALL)
            .setContentIntent(callBackIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_MISSED_CALL, notification)
    }

    /** Posts an already-built notification (for [CallManagerService] updates). */
    fun updateNotification(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    /** Cancels the notification identified by [id]. */
    fun dismissNotification(id: Int) {
        notificationManager.cancel(id)
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }
}
