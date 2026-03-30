package com.app.dialer.core.telecom

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.util.Log

/**
 * Android [ConnectionService] stub for managing self-managed VOIP connections.
 *
 * Registered in AndroidManifest.xml with `BIND_TELECOM_CONNECTION_SERVICE` permission.
 *
 * ### P1 scope
 * Scaffold stub — returns cancelled connections for all requests. Full VOIP
 * connection handling (wired to [CallEventBus] and [AudioRouteManager]) is
 * implemented in Prompt 2 (InCallService module). Standard cellular calls placed
 * via [android.telecom.TelecomManager.placeCall] do not require this service.
 */
class CallConnectionService : ConnectionService() {

    companion object {
        private const val TAG = "CallConnectionService"
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        // P2+: create a CallConnection wired to CallEventBus and AudioRouteManager
        Log.d(TAG, "onCreateOutgoingConnection — P1 stub")
        return Connection.createCanceledConnection()
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        // P2+: create an incoming CallConnection wired to CallEventBus
        Log.d(TAG, "onCreateIncomingConnection — P1 stub")
        return Connection.createCanceledConnection()
    }
}
