package com.app.dialer.core.telecom

import android.net.Uri
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import dagger.hilt.android.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import android.content.Context
import com.app.dialer.core.audio.AudioRouteManager
import java.util.UUID

/**
 * Android [ConnectionService] for managing outgoing and incoming call connections.
 *
 * Registered in AndroidManifest.xml with BIND_TELECOM_CONNECTION_SERVICE permission.
 * This service bridges the Telecom framework and the in-call management layer.
 *
 * Responsibilities:
 * - Create [CallConnection] instances for new calls (outgoing/incoming).
 * - Wire them to [CallEventBus] and [AudioRouteManager] via constructor injection.
 * - Emit [CallEvent.CallAdded] for new calls.
 * - Handle connection failures gracefully.
 *
 * Uses Hilt's [EntryPoint] pattern to access singletons from outside Hilt-aware
 * contexts (ConnectionService is not a Hilt-managed component).
 */
class CallConnectionService : ConnectionService() {

    companion object {
        private const val TAG = "CallConnectionService"

        /**
         * Hilt entry point to access singletons from the Telecom stack context.
         * Called once during [onCreate].
         */
        @EntryPoint
        interface CallServiceEntryPoint {
            fun eventBus(): CallEventBus
            fun audioManager(): AudioRouteManager
        }

        private lateinit var entryPoint: CallServiceEntryPoint
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize Hilt entry point
        entryPoint = EntryPointAccessors.fromApplication(
            this,
            CallServiceEntryPoint::class.java
        )
    }

    /**
     * Creates a connection for an outgoing call.
     *
     * @param connectionManagerPhoneAccount The phone account associated with this request.
     * @param request                       The connection request with phone number, etc.
     * @return A [CallConnection] instance wired to the event bus and audio manager,
     *         or a cancelled connection on error.
     */
    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        return try {
            val address = request?.address?.schemeSpecificPart
                ?: return Connection.createCanceledConnection()

            if (address.isBlank()) {
                Log.w(TAG, "onCreateOutgoingConnection: empty address")
                return Connection.createCanceledConnection()
            }

            val callId = UUID.randomUUID().toString()
            Log.d(
                TAG,
                "onCreateOutgoingConnection: callId=$callId address=$address account=$connectionManagerPhoneAccount"
            )

            val connection = CallConnection(
                callId = callId,
                eventBus = entryPoint.eventBus(),
                audioManager = entryPoint.audioManager()
            )
            connection.setAddress(Uri.parse("tel:$address"), TelecomManager.PRESENTATION_ALLOWED)
            connection.setInitializing()

            // Emit call added event
            entryPoint.eventBus().emitSync(
                CallEvent.CallAdded(
                    callId = callId,
                    number = address,
                    direction = CallDirection.OUTGOING
                )
            )

            // Transition to dialing state
            connection.setDialing()
            connection
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create outgoing connection", e)
            Connection.createCanceledConnection()
        }
    }

    /**
     * Creates a connection for an incoming call.
     *
     * @param connectionManagerPhoneAccount The phone account associated with this request.
     * @param request                       The connection request with caller information.
     * @return A [CallConnection] instance in RINGING state,
     *         or a cancelled connection on error.
     */
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        return try {
            val address = request?.address?.schemeSpecificPart ?: "Unknown"
            val callId = UUID.randomUUID().toString()
            Log.d(
                TAG,
                "onCreateIncomingConnection: callId=$callId address=$address account=$connectionManagerPhoneAccount"
            )

            val connection = CallConnection(
                callId = callId,
                eventBus = entryPoint.eventBus(),
                audioManager = entryPoint.audioManager()
            )
            connection.setAddress(Uri.parse("tel:$address"), TelecomManager.PRESENTATION_ALLOWED)
            connection.setInitializing()

            // Emit call added event
            entryPoint.eventBus().emitSync(
                CallEvent.CallAdded(
                    callId = callId,
                    number = address,
                    direction = CallDirection.INCOMING
                )
            )

            // Transition to ringing state
            connection.setRinging()
            connection
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create incoming connection", e)
            Connection.createCanceledConnection()
        }
    }

    /**
     * Called when creation of an outgoing connection fails.
     * Logs the failure for debugging purposes.
     */
    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.w(
            TAG,
            "onCreateOutgoingConnectionFailed: account=$connectionManagerPhoneAccount address=${request?.address}"
        )
    }

    /**
     * Called when creation of an incoming connection fails.
     * Logs the failure for debugging purposes.
     */
    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.w(
            TAG,
            "onCreateIncomingConnectionFailed: account=$connectionManagerPhoneAccount address=${request?.address}"
        )
    }
}
