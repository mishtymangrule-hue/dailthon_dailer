package com.app.dialer.core.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Handles notification-action broadcasts dispatched from in-call notifications.
 *
 * Declared in AndroidManifest.xml with `android:exported="false"`.
 *
 * Receives intents from notification action buttons (Answer, Decline, End Call,
 * Toggle Speaker, Toggle Mute) and forwards them to [InCallServiceImpl] for processing.
 * Also dispatches Call Control commands via [CallEventBus].
 */
class CallBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ANSWER_CALL    = "com.app.dialer.action.ANSWER_CALL"
        const val ACTION_REJECT_CALL    = "com.app.dialer.action.REJECT_CALL"
        const val ACTION_END_CALL       = "com.app.dialer.action.END_CALL"
        const val ACTION_TOGGLE_MUTE    = "com.app.dialer.action.TOGGLE_MUTE"
        const val ACTION_TOGGLE_SPEAKER = "com.app.dialer.action.TOGGLE_SPEAKER"
        const val EXTRA_PHONE_NUMBER    = "extra_phone_number"

        private const val TAG = "CallBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val inCallService = InCallServiceImpl.instance
        if (inCallService == null) {
            Log.w(TAG, "onReceive: InCallServiceImpl not bound, action ignored: ${intent.action}")
            return
        }

        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)
        Log.d(TAG, "onReceive: ${intent.action} number=$phoneNumber")

        when (intent.action) {
            ACTION_ANSWER_CALL -> {
                if (phoneNumber != null) {
                    inCallService.answerCall(phoneNumber)
                }
            }
            ACTION_REJECT_CALL -> {
                if (phoneNumber != null) {
                    inCallService.rejectCall(phoneNumber)
                }
            }
            ACTION_END_CALL -> {
                if (phoneNumber != null) {
                    inCallService.endCall(phoneNumber)
                }
            }
            ACTION_TOGGLE_MUTE -> {
                Log.d(TAG, "Toggle mute requested")
            }
            ACTION_TOGGLE_SPEAKER -> {
                Log.d(TAG, "Toggle speaker requested")
            }
            else -> {
                Log.w(TAG, "Unknown action: ${intent.action}")
            }
        }
    }
}

/** Typed command model — used in P2+ when wired to [InCallServiceImpl]. */
sealed class CallControlCommand {
    object AnswerCall    : CallControlCommand()
    object RejectCall    : CallControlCommand()
    object EndCall       : CallControlCommand()
    object ToggleMute    : CallControlCommand()
    object ToggleSpeaker : CallControlCommand()
}
