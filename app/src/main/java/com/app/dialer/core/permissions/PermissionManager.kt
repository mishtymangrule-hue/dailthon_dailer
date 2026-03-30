package com.app.dialer.core.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.MutableState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Sealed result type for individual permission checks.
 */
sealed class PermissionResult {
    /** Permission has been granted by the user. */
    object Granted : PermissionResult()

    /** Permission was denied; the user can still be asked again. */
    data class Denied(val shouldShowRationale: Boolean) : PermissionResult()

    /**
     * Permission was permanently denied. The user must navigate to system settings
     * to manually grant it.
     */
    object PermanentlyDenied : PermissionResult()
}

/**
 * The complete set of permissions required by a default dialer replacement.
 *
 * READ_PHONE_NUMBERS was added in API 26. On API 33+ it is separated from
 * READ_PHONE_STATE, but requesting both is safe across the full API range.
 */
val ALL_DIALER_PERMISSIONS: List<String> = buildList {
    add(Manifest.permission.CALL_PHONE)
    add(Manifest.permission.READ_PHONE_STATE)
    add(Manifest.permission.READ_CONTACTS)
    add(Manifest.permission.WRITE_CONTACTS)
    add(Manifest.permission.READ_CALL_LOG)
    add(Manifest.permission.WRITE_CALL_LOG)
    add(Manifest.permission.RECORD_AUDIO)

    // READ_PHONE_NUMBERS available from API 26 (our minSdk), required for
    // reading the device's own phone number.
    add(Manifest.permission.READ_PHONE_NUMBERS)

    // ANSWER_PHONE_CALLS available from API 26
    add(Manifest.permission.ANSWER_PHONE_CALLS)
}

/**
 * Stable state holder for the dialer permission group.
 *
 * Wraps Accompanist's [MultiplePermissionsState] and adds:
 * - Per-permission [PermissionResult] classification.
 * - Rationale tracking for each individual permission.
 * - First-request detection to distinguish "never asked" from "permanently denied".
 * - Helper to open the system settings page for permanent denials.
 *
 * Note on first-call semantics: before the first permission request is made,
 * [PermissionStatus.Denied.shouldShowRationale] returns false — identical to
 * the permanently-denied state. We track whether a request has been launched
 * via [hasRequestedOnce] to return [PermissionResult.Denied] (not
 * [PermissionResult.PermanentlyDenied]) until the user has been asked at least once.
 */
@Stable
@OptIn(ExperimentalPermissionsApi::class)
class PermissionManager(
    private val multiplePermissionsState: MultiplePermissionsState,
    private val context: Context,
    private val hasRequestedOnceState: MutableState<Boolean>
) {
    /** Tracks whether [requestAllDialerPermissions] has been called at least once. */
    private var hasRequestedOnce: Boolean by hasRequestedOnceState

    /** True when every permission in [ALL_DIALER_PERMISSIONS] is granted. */
    val allGranted: Boolean
        get() = multiplePermissionsState.allPermissionsGranted

    /** True when at least one permission still needs to be requested. */
    val shouldShowRationale: Boolean
        get() = multiplePermissionsState.shouldShowRationale

    /** Returns the [PermissionResult] for the given [permission] string. */
    fun checkPermission(permission: String): PermissionResult {
        val permState = multiplePermissionsState.permissions
            .firstOrNull { it.permission == permission }
            ?: return PermissionResult.PermanentlyDenied

        return when (val status = permState.status) {
            PermissionStatus.Granted -> PermissionResult.Granted
            is PermissionStatus.Denied -> {
                when {
                    status.shouldShowRationale -> PermissionResult.Denied(shouldShowRationale = true)
                    !hasRequestedOnce -> PermissionResult.Denied(shouldShowRationale = false)
                    else -> PermissionResult.PermanentlyDenied
                }
            }
        }
    }

    /**
     * Launches the system permission request dialog for the given single [permissionState].
     *
     * This delegates to Accompanist's per-permission state launcher which
     * internally uses ActivityResultContracts.RequestPermission.
     */
    fun requestPermission(permissionState: PermissionState) {
        hasRequestedOnce = true
        permissionState.launchPermissionRequest()
    }

    /** Launches the permission request dialog for all dialer permissions at once. */
    fun requestAllDialerPermissions() {
        hasRequestedOnce = true
        multiplePermissionsState.launchMultiplePermissionRequest()
    }

    /**
     * Opens the application's system settings page so the user can manually
     * grant permanently-denied permissions.
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

/**
 * Remembers and returns a [PermissionManager] scoped to the current composition.
 *
 * Registers Accompanist's permission request launchers and links them to
 * the returned manager.
 *
 * @param onPermissionsResult Optional callback invoked after each batch permission result.
 *                            The [Map] maps each permission string to its granted status.
 */
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun rememberPermissionManager(
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {}
): PermissionManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasRequestedOnceState = rememberSaveable { mutableStateOf(false) }

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = ALL_DIALER_PERMISSIONS,
        onPermissionsResult = onPermissionsResult
    )

    return remember(multiplePermissionsState, hasRequestedOnceState) {
        PermissionManager(
            multiplePermissionsState = multiplePermissionsState,
            context = context,
            hasRequestedOnceState = hasRequestedOnceState
        )
    }
}
