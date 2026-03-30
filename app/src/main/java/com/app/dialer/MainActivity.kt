package com.app.dialer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.app.dialer.core.permissions.rememberPermissionManager
import com.app.dialer.presentation.navigation.AppNavGraph
import com.app.dialer.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity that hosts the entire Compose UI.
 *
 * Responsibilities:
 * - Enables edge-to-edge display (transparent status + nav bar).
 * - Applies [AppTheme] as the root theme wrapper.
 * - Hosts [AppNavGraph] which contains all navigation destinations.
 * - Requests all dialer-required runtime permissions on launch via [PermissionManager].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow the app content to draw behind system bars (edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // enableEdgeToEdge sets up transparent status + navigation bars with
        // correct scrim/icon contrast automatically (API 26+ compatible via AndroidX)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DialerApp()
                }
            }
        }
    }
}

/**
 * Root composable that wires together the permission manager and the navigation graph.
 *
 * Permissions are requested once when this composable first enters the composition.
 * The NavGraph remains mounted regardless of permission state — individual features
 * handle their own graceful degradation when permissions are absent.
 */
@Composable
private fun DialerApp() {
    val navController = rememberNavController()

    val permissionManager = rememberPermissionManager(
        onPermissionsResult = { results ->
            // Results handled per-screen; global reaction not needed at this level.
        }
    )

    // Request all dialer permissions on the first frame.
    // Accompanist ensures this only shows the system dialog once per session
    // and does not re-trigger on recomposition.
    LaunchedEffect(Unit) {
        if (!permissionManager.allGranted) {
            permissionManager.requestAllDialerPermissions()
        }
    }

    AppNavGraph(navController = navController)
}
