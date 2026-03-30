package com.app.dialer.core.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles broadcast actions dispatched from call-control notifications.
 *
 * Each action maps to a control command that is forwarded to
 * [InCallServiceImpl] via its companion-object instance handle.
 *
 * All actions must be declared in AndroidManifest.xml with
 * `android:exported="false"` and matched by explicit intent.
 */
class CallBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ANSWER_CALL   = "com.app.dialer.action.ANSWER_CALL"
        const val ACTION_REJECT_CALL   = "com.app.dialer.action.REJECT_CALL"
        const val ACTION_END_CALL      = "com.app.dialer.action.END_CALL"
        const val ACTION_TOGGLE_MUTE   = "com.app.dialer.action.TOGGLE_MUTE"
        const val ACTION_TOGGLE_SPEAKER = "com.app.dialer.action.TOGGLE_SPEAKER"

        /** Optional extra carrying the target phone number (e.g. for answer/decline). */
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val service = InCallServiceImpl.instance ?: return

        when (intent.action) {
            ACTION_ANSWER_CALL    -> service.answerFirstRinging()
            ACTION_REJECT_CALL    -> service.rejectFirstRinging()
            ACTION_END_CALL       -> service.endAllActiveCalls()
            ACTION_TOGGLE_MUTE    -> service.toggleMute()
            ACTION_TOGGLE_SPEAKER -> service.toggleSpeaker()
        }
    }
}

/** Typed command model forwarded from [CallBroadcastReceiver] to [InCallServiceImpl]. */
sealed class CallControlCommand {
    object AnswerCall    : CallControlCommand()
    object RejectCall    : CallControlCommand()
    object EndCall       : CallControlCommand()
    object ToggleMute    : CallControlCommand()
    object ToggleSpeaker : CallControlCommand()
}
