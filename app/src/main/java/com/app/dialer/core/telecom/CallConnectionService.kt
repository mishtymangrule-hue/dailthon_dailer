package com.app.dialer.core.telecom

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.util.Log
import com.app.dialer.core.audio.AudioRouteManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.UUID

/**
 * Android [ConnectionService] that creates [CallConnection] instances on behalf of
 * the Telecom framework.
 *
 * ### Hilt integration
 * [ConnectionService] is a system-instantiated Android component, so standard
 * `@AndroidEntryPoint` constructor injection is not available. Instead, this class
 * uses the Hilt EntryPoint pattern to resolve [CallEventBus] and [AudioRouteManager]
 * from the application's [SingletonComponent] at runtime.
 *
 * ### Manifest registration
 * Register in `AndroidManifest.xml`:
 * ```xml
 * <service android:name=".core.telecom.CallConnectionService"
 *          android:permission="android.permission.BIND_TELECOM_CONNECTION_SERVICE"
 *          android:exported="true">
 *     <intent-filter>
 *         <action android:name="android.telecom.ConnectionService"/>
 *     </intent-filter>
 * </service>
 * ```
 */
class CallConnectionService : ConnectionService() {

    // ─── Hilt EntryPoint ──────────────────────────────────────────────────────

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CallConnectionServiceEntryPoint {
        fun callEventBus(): CallEventBus
        fun audioRouteManager(): AudioRouteManager
    }

    private val entryPoint: CallConnectionServiceEntryPoint by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            CallConnectionServiceEntryPoint::class.java
        )
    }

    private val eventBus: CallEventBus get() = entryPoint.callEventBus()
    private val audioRouteManager: AudioRouteManager get() = entryPoint.audioRouteManager()

    companion object {
        private const val TAG = "CallConnService"
        private const val UNKNOWN_NUMBER = "Unknown"
    }

    // ─── Outgoing calls ───────────────────────────────────────────────────────

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val callId = UUID.randomUUID().toString()
        val number = request?.address?.schemeSpecificPart?.takeIf { it.isNotBlank() }
            ?: UNKNOWN_NUMBER

        Log.d(TAG, "onCreateOutgoingConnection: callId=$callId number=$number")

        val connection = CallConnection(
            eventBus = eventBus,
            audioRouteManager = audioRouteManager,
            callId = callId
        ).apply {
            setDialing()
        }

        eventBus.emitSync(
            CallEvent.CallAdded(
                callId = callId,
                number = number,
                direction = CallDirection.OUTGOING
            )
        )

        return connection
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        val number = request?.address?.schemeSpecificPart ?: UNKNOWN_NUMBER
        Log.w(TAG, "onCreateOutgoingConnectionFailed: number=$number")
        // Synthesise a unique ID so CallStateManager can track the failure.
        val callId = "failed_out_${number}_${System.currentTimeMillis()}"
        eventBus.emitSync(
            CallEvent.CallStateChanged(callId = callId, state = CallState.DISCONNECTED)
        )
    }

    // ─── Incoming calls ───────────────────────────────────────────────────────

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val callId = UUID.randomUUID().toString()
        val number = request?.address?.schemeSpecificPart?.takeIf { it.isNotBlank() }
            ?: UNKNOWN_NUMBER

        Log.d(TAG, "onCreateIncomingConnection: callId=$callId number=$number")

        val connection = CallConnection(
            eventBus = eventBus,
            audioRouteManager = audioRouteManager,
            callId = callId
        ).apply {
            setRinging()
        }

        eventBus.emitSync(
            CallEvent.CallAdded(
                callId = callId,
                number = number,
                direction = CallDirection.INCOMING
            )
        )

        return connection
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        val number = request?.address?.schemeSpecificPart ?: UNKNOWN_NUMBER
        Log.w(TAG, "onCreateIncomingConnectionFailed: number=$number")
        val callId = "failed_in_${number}_${System.currentTimeMillis()}"
        eventBus.emitSync(
            CallEvent.CallStateChanged(callId = callId, state = CallState.DISCONNECTED)
        )
    }
}
