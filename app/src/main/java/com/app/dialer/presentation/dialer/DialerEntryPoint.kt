package com.app.dialer.presentation.dialer

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dialer.core.permissions.ALL_DIALER_PERMISSIONS
import com.app.dialer.core.permissions.DialerPermissionScreen
import com.app.dialer.core.utils.OEMCompatHelper
import com.app.dialer.domain.model.SimCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

private val DarkBackground = Color(0xFF1C1C1E)
private val ElectricBlue   = Color(0xFF007AFF)
private val TextPrimary    = Color(0xFFFFFFFF)
private val TextSecondary  = Color(0xFF8E8E93)

/**
 * Hilt entry point that allows [DialerEntryPoint] composable to access
 * application-scoped singletons without `@AndroidEntryPoint` on the Activity.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface DialerEntryPointHelper {
    fun oemCompatHelper(): OEMCompatHelper
}

/**
 * Gate composable that sits in front of [DialerScreen].
 *
 * ### Logic
 * 1. **Permissions not granted** → shows [DialerPermissionScreen].
 * 2. **Permissions granted, not default dialer** → shows a "Set as default dialer"
 *    prompt. The user can dismiss and proceed to the dialer anyway (non-blocking).
 * 3. **Permissions granted + is/was dismissed as default dialer** → shows [DialerScreen].
 *
 * The default-dialer check is advisory: the dialer screen is accessible even if
 * the app is not currently the default. The in-call service will simply not be
 * invoked by the system for calls handled by another dialer.
 *
 * @param onNavigateToInCall Forwarded to [DialerScreen].
 * @param onNavigateToSettings Forwarded to [DialerScreen].
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DialerEntryPoint(
    onNavigateToInCall: (String, SimCard?) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    // Resolve OEMCompatHelper via entry point (no @AndroidEntryPoint on Activity needed)
    val oemHelper = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            DialerEntryPointHelper::class.java
        ).oemCompatHelper()
    }

    // ── Permission state ─────────────────────────────────────────────────────
    val permissionsState = rememberMultiplePermissionsState(
        permissions = ALL_DIALER_PERMISSIONS
    )
    val allGranted = permissionsState.allPermissionsGranted

    // ── Default-dialer prompt state ───────────────────────────────────────────
    var defaultDialerDismissed by remember { mutableStateOf(false) }
    val isDefault = remember(defaultDialerDismissed) { oemHelper.isDefaultDialer }

    val defaultDialerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // RESULT_OK means the user accepted; RESULT_CANCELED means they declined.
        // Either way, we dismiss the prompt and proceed.
        defaultDialerDismissed = true
    }

    // ── UI state machine ──────────────────────────────────────────────────────
    AnimatedContent(
        targetState = when {
            !allGranted -> EntryState.PERMISSIONS
            !isDefault && !defaultDialerDismissed -> EntryState.DEFAULT_DIALER
            else -> EntryState.DIALER
        },
        label = "DialerEntryPoint"
    ) { state ->
        when (state) {
            EntryState.PERMISSIONS -> {
                DialerPermissionScreen(
                    onPermissionsGranted = { /* recomposition will advance state */ }
                )
            }

            EntryState.DEFAULT_DIALER -> {
                DefaultDialerPrompt(
                    onSetDefault = {
                        defaultDialerLauncher.launch(oemHelper.buildDefaultDialerIntent())
                    },
                    onSkip = { defaultDialerDismissed = true }
                )
            }

            EntryState.DIALER -> {
                DialerScreen(
                    onNavigateToInCall = onNavigateToInCall,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}

private enum class EntryState { PERMISSIONS, DEFAULT_DIALER, DIALER }

// ─── Default dialer prompt ─────────────────────────────────────────────────────

@Composable
private fun DefaultDialerPrompt(
    onSetDefault: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set as Default Dialer",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Set Dailathon as the default dialer to manage incoming calls, " +
                       "call screening, and call controls.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSetDefault,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text(
                    text = "Set as Default",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C2C2E),
                    contentColor = TextSecondary
                )
            ) {
                Text(
                    text = "Maybe Later",
                    fontSize = 15.sp
                )
            }
        }
    }
}
