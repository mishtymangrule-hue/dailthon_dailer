package com.app.dialer.domain.model

/**
 * Represents a validated and formatted phone number for the dialer module.
 *
 * @param rawInput    The unmodified string as typed by the user.
 * @param formatted   Locale-aware formatted string (e.g. "+91 98765 43210").
 *                    Equals [rawInput] when no formatting could be applied.
 * @param isValid     True when the number passes length and format validation.
 * @param countryCode ISO 3166-1 alpha-2 country code used for formatting (e.g. "IN", "US").
 */
data class PhoneNumber(
    val rawInput: String,
    val formatted: String,
    val isValid: Boolean,
    val countryCode: String
) {
    companion object {
        /** Sentinel representing an absent / empty phone number. */
        val EMPTY = PhoneNumber(
            rawInput = "",
            formatted = "",
            isValid = false,
            countryCode = ""
        )
    }
}
