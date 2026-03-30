package com.app.dialer.domain.model

/**
 * Represents a physical SIM card slot present on the device.
 *
 * @param slotIndex      0-based index of the SIM slot in the device hardware.
 * @param subscriptionId Android [android.telephony.SubscriptionManager] subscription ID.
 *                       Pass this to [android.telecom.TelecomManager] when initiating calls
 *                       on a specific SIM.
 * @param displayName    Human-readable name for the SIM (e.g. "SIM 1", carrier label).
 * @param carrierName    Network operator name reported by the subscription.
 * @param isDefault      True when this SIM is the default for outgoing calls.
 * @param isActive       True when the SIM is present and the subscription is active.
 */
data class SimCard(
    val slotIndex: Int,
    val subscriptionId: Int,
    val displayName: String,
    val carrierName: String,
    val isDefault: Boolean,
    val isActive: Boolean
)
