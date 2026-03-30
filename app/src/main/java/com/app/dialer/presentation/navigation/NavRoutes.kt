package com.app.dialer.presentation.navigation

/**
 * Sealed class defining all navigation routes in the Dialer app.
 * Each object represents one destination in the NavHost.
 */
sealed class NavRoutes(val route: String) {

    /** Keypad / dial screen */
    object Dialer : NavRoutes("dialer")

    /** Contacts list screen */
    object Contacts : NavRoutes("contacts")

    /** Call log screen */
    object CallLog : NavRoutes("call_log")

    /** In-call screen — accepts an optional call ID argument */
    object InCall : NavRoutes("in_call/{callId}") {
        const val ARG_CALL_ID = "callId"
        fun createRoute(callId: String): String = "in_call/$callId"
    }

    /** App settings screen */
    object Settings : NavRoutes("settings")
}
