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
 * ### P1 scope
 * Scaffold stub — logs intent action only. Full call-control forwarding
 * (answer, reject, mute, speaker) via [InCallServiceImpl] is implemented
 * in Prompt 2 when the full in-call notification is built.
 */
class CallBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ANSWER_CALL    = "com.app.dialer.action.ANSWER_CALL"
        const val ACTION_REJECT_CALL    = "com.app.dialer.action.REJECT_CALL"
        const val ACTION_END_CALL       = "com.app.dialer.action.END_CALL"
        const val ACTION_TOGGLE_MUTE    = "com.app.dialer.action.TOGGLE_MUTE"
        const val ACTION_TOGGLE_SPEAKER = "com.app.dialer.action.TOGGLE_SPEAKER"
        const val EXTRA_PHONE_NUMBER    = "extra_phone_number"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // P2+: forward action to InCallServiceImpl control shims
        Log.d("CallBroadcastReceiver", "onReceive: ${intent.action} — P1 stub")
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
