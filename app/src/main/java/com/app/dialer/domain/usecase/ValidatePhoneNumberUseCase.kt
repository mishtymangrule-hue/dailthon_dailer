package com.app.dialer.domain.usecase

import android.telephony.PhoneNumberUtils
import javax.inject.Inject

/**
 * Pure-logic use case that determines whether a string constitutes a valid
 * dialable phone number.
 *
 * Uses only platform APIs ([PhoneNumberUtils]) — no third-party libphonenumber.
 *
 * ### Validation rules (all must pass)
 * 1. The string must contain between 7 and 15 digit characters (E.164 maximum is 15).
 * 2. After stripping non-dialable characters (spaces, dashes, parentheses),
 *    [PhoneNumberUtils.isGlobalPhoneNumber] must return true.
 *
 * Leading `+` (country code prefix) and common formatting characters (spaces,
 * dashes, parentheses) are handled correctly by [PhoneNumberUtils].
 */
class ValidatePhoneNumberUseCase @Inject constructor() {

    /**
     * @param phoneNumber Raw or formatted phone number string to validate.
     * @return True if [phoneNumber] is a plausibly dialable global phone number.
     */
    operator fun invoke(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false

        // Count only numeric digits — ignore +, spaces, dashes, parens, extensions
        val digitCount = phoneNumber.count { it.isDigit() }
        if (digitCount !in 7..15) return false

        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)
    }
}
