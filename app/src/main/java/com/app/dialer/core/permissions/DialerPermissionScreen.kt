package com.app.dialer.core.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

private val DarkBackground = Color(0xFF1C1C1E)
private val ElectricBlue   = Color(0xFF007AFF)
private val SoftTeal       = Color(0xFF5AC8FA)
private val CardBackground = Color(0xFF2C2C2E)
private val TextPrimary    = Color(0xFFFFFFFF)
private val TextSecondary  = Color(0xFF8E8E93)

private data class PermissionGroup(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val permissions: List<String>
)

private val PERMISSION_GROUPS = listOf(
    PermissionGroup(
        title = "Phone",
        description = "Make and receive calls, manage call state and SIM information.",
        icon = Icons.Filled.Call,
        permissions = listOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.ANSWER_PHONE_CALLS
        )
    ),
    PermissionGroup(
        title = "Contacts",
        description = "Look up contact names for incoming and outgoing calls.",
        icon = Icons.Filled.Person,
        permissions = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
    ),
    PermissionGroup(
        title = "Call History",
        description = "Read and record your recent calls so they appear in the log.",
        icon = Icons.Filled.History,
        permissions = listOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
        )
    ),
    PermissionGroup(
        title = "Microphone",
        description = "Capture audio during calls.",
        icon = Icons.Filled.Mic,
        permissions = listOf(Manifest.permission.RECORD_AUDIO)
    )
)

/**
 * Full-screen permission request UI shown when the dialer has not yet been
 * granted its required permissions.
 *
 * Uses Accompanist [rememberMultiplePermissionsState] to request all
 * [ALL_DIALER_PERMISSIONS] at once. After the user grants permissions, the
 * parent composable ([DialerEntryPoint]) detects the state change and navigates
 * to the main dialer UI.
 *
 * @param onPermissionsGranted Called once when all permissions have been granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DialerPermissionScreen(
    onPermissionsGranted: () -> Unit = {}
) {
    val context = LocalContext.current

    var hasRequestedOnce by remember { mutableStateOf(false) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = ALL_DIALER_PERMISSIONS
    ) { results ->
        if (results.values.all { it }) onPermissionsGranted()
    }

    // If all permissions have been granted, notify parent
    if (permissionsState.allPermissionsGranted) {
        onPermissionsGranted()
        return
    }

    // Determine if any permission is permanently denied (denied + no rationale after request)
    val hasPermanentlyDenied = hasRequestedOnce &&
        permissionsState.permissions.any { perm ->
            !perm.status.isGranted && !perm.status.shouldShowRationale
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Filled.Call,
            contentDescription = null,
            tint = ElectricBlue,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Permissions Required",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Dailathon needs the following permissions to function as your default dialer.",
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Permission group cards ─────────────────────────────────────────────
        PERMISSION_GROUPS.forEach { group ->
            val allGranted = group.permissions.all { permission ->
                permissionsState.permissions
                    .firstOrNull { it.permission == permission }
                    ?.status?.isGranted == true
            }

            PermissionGroupCard(
                group = group,
                isGranted = allGranted
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Primary action ────────────────────────────────────────────────────
        AnimatedVisibility(visible = !hasPermanentlyDenied) {
            Button(
                onClick = {
                    hasRequestedOnce = true
                    permissionsState.launchMultiplePermissionRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text(
                    text = if (hasRequestedOnce) "Try Again" else "Grant Permissions",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── Settings fallback (permanently denied) ───────────────────────────
        AnimatedVisibility(visible = hasPermanentlyDenied) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Some permissions were permanently denied.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftTeal)
                ) {
                    Text(
                        text = "Open App Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PermissionGroupCard(
    group: PermissionGroup,
    isGranted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = group.icon,
                contentDescription = null,
                tint = if (isGranted) Color(0xFF34C759) else ElectricBlue,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = group.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Text(
                    text = "✓",
                    color = Color(0xFF34C759),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
