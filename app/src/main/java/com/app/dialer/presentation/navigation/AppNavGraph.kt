package com.app.dialer.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.app.dialer.presentation.calllog.CallLogScreen
import com.app.dialer.presentation.contacts.ContactsScreen
import com.app.dialer.presentation.dialer.DialerScreen
import com.app.dialer.presentation.incall.InCallScreen
import com.app.dialer.presentation.settings.SettingsScreen

/**
 * Root navigation graph for the Dialer app.
 *
 * Wraps a [Scaffold] containing the [BottomNavBar] and a [NavHost] with all
 * registered destinations. The bottom bar is hidden on the InCall screen.
 *
 * @param navController  The [NavHostController] driving navigation.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation during an active call.
    // In Navigation Compose, destination.route returns the route *template* string
    // (e.g. "in_call/{callId}"), not the instantiated route with argument values.
    // Both an exact equality check against NavRoutes.InCall.route and this startsWith
    // check would work; startsWith is preferred as a defensive guard in case the
    // route template is ever changed or extended.
    val showBottomBar = !currentRoute.orEmpty().startsWith("in_call/")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Dialer.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavRoutes.Dialer.route) {
                DialerScreen(
                    onNavigateToCall = { callId ->
                        navController.navigate(NavRoutes.InCall.createRoute(callId))
                    }
                )
            }

            composable(route = NavRoutes.Contacts.route) {
                ContactsScreen(
                    onContactClick = { callId ->
                        navController.navigate(NavRoutes.InCall.createRoute(callId))
                    }
                )
            }

            composable(route = NavRoutes.CallLog.route) {
                CallLogScreen(
                    onCallClick = { callId ->
                        navController.navigate(NavRoutes.InCall.createRoute(callId))
                    }
                )
            }

            composable(
                route = NavRoutes.InCall.route,
                arguments = listOf(
                    navArgument(NavRoutes.InCall.ARG_CALL_ID) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val callId = backStackEntry.arguments
                    ?.getString(NavRoutes.InCall.ARG_CALL_ID)
                    .orEmpty()
                InCallScreen(
                    callId = callId,
                    onCallEnded = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = NavRoutes.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
