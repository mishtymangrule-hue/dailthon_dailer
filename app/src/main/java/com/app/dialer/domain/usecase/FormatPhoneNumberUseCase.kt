package com.app.dialer.domain.usecase

import android.telephony.PhoneNumberUtils
import com.app.dialer.domain.model.DialerPhoneNumber
import java.util.Locale
import javax.inject.Inject

/**
 * Pure-logic use case that formats a raw dial-pad input string into a
 * validated [DialerPhoneNumber] domain model.
 *
 * Uses only platform APIs ([PhoneNumberUtils]) — no third-party libphonenumber.
 *
 * ### Formatting rules
 * - Empty / blank input → returns [DialerPhoneNumber.EMPTY].
 * - Strips characters that are not digits, `+`, `,`, `;`, or `p` (pause extensions).
 * - Preserves a leading `+` for international numbers.
 * - Extension separators (`,`, `;`, `p`) are preserved in [DialerPhoneNumber.rawInput]
 *   but stripped before the validity length check.
 * - Delegates formatting to [PhoneNumberUtils.formatNumber] with the given [countryCode].
 *   Falls back to the stripped input when the platform formatter returns null.
 * - Valid range: 7–15 dialable digits (E.164 without country-code prefix can be shorter;
 *   15 is the ITU-T E.164 maximum).
 *
 * @param countryCode ISO 3166-1 alpha-2 country code used for formatting (default "IN").
 */
class FormatPhoneNumberUseCase @Inject constructor() {

    operator fun invoke(
        rawInput: String,
        countryCode: String = "IN"
    ): DialerPhoneNumber {
        if (rawInput.isBlank()) return DialerPhoneNumber.EMPTY

        // Normalise: keep digits, leading '+', and extension separators , ; p
        val normalised = buildString {
            rawInput.forEachIndexed { index, ch ->
                when {
                    ch == '+' && index == 0 -> append(ch)          // leading + only
                    ch.isDigit()            -> append(ch)
                    ch == ',' || ch == ';' || ch == 'p' -> append(ch)  // pause/wait
                }
            }
        }

        if (normalised.isEmpty()) return DialerPhoneNumber.EMPTY

        // Dialable digits only (strip extension separators for length/validity checks)
        val dialableOnly = normalised.filter { it.isDigit() || it == '+' }
        val digitCount = dialableOnly.count { it.isDigit() }

        val isValid = digitCount in 7..15

        // Platform formatter — returns null for sequences it cannot recognise
        val formatted: String = PhoneNumberUtils.formatNumber(
            normalised,
            countryCode.uppercase(Locale.ROOT)
        ) ?: normalised

        return DialerPhoneNumber(
            rawInput = rawInput,
            formatted = formatted,
            isValid = isValid,
            countryCode = countryCode.uppercase(Locale.ROOT)
        )
    }
}
