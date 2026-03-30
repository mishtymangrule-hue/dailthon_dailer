package com.app.dialer.core.utils

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encapsulates OEM-specific and system-level compatibility checks needed before
 * the dialer can function correctly.
 *
 * ### Checks exposed
 * - **Default dialer**: whether this app currently holds the default-dialer role.
 * - **Battery optimisation**: whether the system has exempted this app from
 *   Doze-mode battery restrictions (important for incoming-call reliability).
 * - **System overlay**: whether the app holds `SYSTEM_ALERT_WINDOW` permission,
 *   required on some OEM ROMs to show incoming-call UI over the lock screen.
 *
 * ### Role vs Telecom API
 * On API 29+, the preferred way to become the default dialer is via
 * [RoleManager.createRequestRoleIntent]. On older APIs, we fall back to
 * [TelecomManager.ACTION_CHANGE_DEFAULT_DIALER].
 */
@Singleton
class OEMCompatHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val telecomManager: TelecomManager by lazy {
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }

    companion object {
        private const val TAG = "OEMCompatHelper"
    }

    // ─── Default dialer ───────────────────────────────────────────────────────

    /**
     * Returns `true` if this app is currently the system's default dialer.
     */
    val isDefaultDialer: Boolean
        get() = telecomManager.defaultDialerPackage == context.packageName

    /**
     * Builds an [Intent] that leads the user through the "set default dialer" flow.
     *
     * - API 29+: uses [RoleManager] (shows a system dialog, no Activity result needed).
     * - API 26–28: uses [TelecomManager.ACTION_CHANGE_DEFAULT_DIALER].
     *
     * The caller must start this intent with `startActivityForResult` (or
     * `rememberLauncherForActivityResult` in Compose) and react to the result.
     */
    fun buildDefaultDialerIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        } else {
            @Suppress("DEPRECATION")
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(
                    TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                    context.packageName
                )
            }
        }
    }

    // ─── Battery optimisation ─────────────────────────────────────────────────

    /**
     * Returns `true` if this app is already exempt from battery optimisations
     * (i.e. whitelisted from Doze). When `false`, incoming calls may be silently
     * dropped in Doze mode on some OEMs.
     */
    val isBatteryOptimisationIgnored: Boolean
        get() {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(context.packageName)
        }

    /**
     * Returns an [Intent] that opens the battery-optimisation settings page for
     * this app so the user can whitelist it.
     */
    fun buildBatteryOptimisationIntent(): Intent =
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }

    // ─── System overlay ───────────────────────────────────────────────────────

    /**
     * Returns `true` if the app holds [android.Manifest.permission.SYSTEM_ALERT_WINDOW].
     *
     * Required on some OEM firmwares (Xiaomi, Oppo, Vivo) to display the incoming-
     * call full-screen UI over the lock screen when the screen is off.
     */
    val canDrawOverlays: Boolean
        get() = Settings.canDrawOverlays(context)

    /**
     * Returns an [Intent] that opens the "Draw over other apps" settings screen
     * so the user can grant the overlay permission.
     */
    fun buildOverlaySettingsIntent(): Intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        }

    // ─── Telephony availability ───────────────────────────────────────────────

    /**
     * Returns `true` if the device has a telephony radio (hardware phone capability).
     * Tablets and some Chromebooks may return `false`.
     */
    val hasTelephonyFeature: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

    // ─── Log helper ──────────────────────────────────────────────────────────

    /**
     * Logs the current OEM compat state at DEBUG level. Useful during QA.
     */
    fun logCompatState() {
        Log.d(TAG, buildString {
            appendLine("=== OEM Compat State ===")
            appendLine("  isDefaultDialer            : $isDefaultDialer")
            appendLine("  isBatteryOptimisationIgnored: $isBatteryOptimisationIgnored")
            appendLine("  canDrawOverlays            : $canDrawOverlays")
            appendLine("  hasTelephonyFeature        : $hasTelephonyFeature")
        })
    }
}
