package com.app.dialer.core.utils

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PhoneNumberFormatterTest {

    private lateinit var mockContext: Context
    private lateinit var mockTelephonyManager: TelephonyManager
    private lateinit var formatter: PhoneNumberFormatter

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockTelephonyManager = mockk(relaxed = true)
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager

        formatter = PhoneNumberFormatter(mockContext)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ─── stripFormatting ───────────────────────────────────────────────────────

    @Test
    fun `stripFormatting removes dashes and spaces`() {
        assertEquals("1234567890", formatter.stripFormatting("123-456-7890"))
    }

    @Test
    fun `stripFormatting preserves leading plus for international prefix`() {
        assertEquals("+15551234567", formatter.stripFormatting("+1 (555) 123-4567"))
    }

    @Test
    fun `stripFormatting removes parentheses`() {
        assertEquals("9876543210", formatter.stripFormatting("(987) 654-3210"))
    }

    @Test
    fun `stripFormatting returns empty string for empty input`() {
        assertEquals("", formatter.stripFormatting(""))
    }

    @Test
    fun `stripFormatting returns empty string for non-digit input`() {
        assertEquals("", formatter.stripFormatting("abc-def"))
    }

    @Test
    fun `stripFormatting does not preserve mid-string plus sign`() {
        // Only a leading '+' should survive; the one at index 6 is stripped
        assertEquals("+919191234567", formatter.stripFormatting("+91919+1234567"))
    }

    @Test
    fun `stripFormatting handles already clean number`() {
        assertEquals("9876543210", formatter.stripFormatting("9876543210"))
    }

    @Test
    fun `stripFormatting preserves leading plus with no other characters`() {
        assertEquals("+", formatter.stripFormatting("+"))
    }

    // ─── format (via mocked PhoneNumberUtils) ─────────────────────────────────

    @Test
    fun `format returns blank input unchanged`() {
        assertEquals("   ", formatter.format("   "))
    }

    @Test
    fun `format returns empty string unchanged`() {
        assertEquals("", formatter.format(""))
    }

    @Test
    fun `format re-appends comma extension suffix after formatted number`() {
        mockkStatic(PhoneNumberUtils::class)
        every { PhoneNumberUtils.formatNumber(any<String>(), any<String>()) } returns "98765 43210"

        val result = formatter.format("9876543210,1234")

        assertTrue("Result should contain the extension suffix", result.endsWith(",1234"))
        unmockkAll()
        // Re-setup context mock removed by unmockkAll
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager
    }

    @Test
    fun `format re-appends semicolon extension suffix after formatted number`() {
        mockkStatic(PhoneNumberUtils::class)
        every { PhoneNumberUtils.formatNumber(any<String>(), any<String>()) } returns "98765 43210"

        val result = formatter.format("9876543210;9")

        assertTrue("Result should contain the wait extension", result.endsWith(";9"))
        unmockkAll()
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager
    }

    @Test
    fun `format falls back to heuristic when PhoneNumberUtils returns null`() {
        mockkStatic(PhoneNumberUtils::class)
        every { PhoneNumberUtils.formatNumber(any<String>(), any<String>()) } returns null

        // 10-digit number → heuristic: "AAA BBB CCCC"
        val result = formatter.format("9876543210")
        assertEquals("987 654 3210", result)

        unmockkAll()
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager
    }

    @Test
    fun `format heuristic handles 5-digit number as AAA BBBB`() {
        mockkStatic(PhoneNumberUtils::class)
        every { PhoneNumberUtils.formatNumber(any<String>(), any<String>()) } returns null

        val result = formatter.format("12345")
        assertEquals("123 45", result)

        unmockkAll()
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager
    }

    @Test
    fun `format heuristic handles 11-digit number as AA BBBBB CCCCC`() {
        mockkStatic(PhoneNumberUtils::class)
        every { PhoneNumberUtils.formatNumber(any<String>(), any<String>()) } returns null

        val result = formatter.format("91987654321")
        assertEquals("91 98765 4321", result)

        unmockkAll()
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager
    }

    @Test
    fun `format returns raw input on exception`() {
        mockkStatic(PhoneNumberUtils::class)
        mockkStatic(Log::class)
        every { PhoneNumberUtils.formatNumber(any<String>(), any<String>()) } throws RuntimeException("boom")
        every { Log.w(any(), any<String>(), any()) } returns 0

        val rawInput = "9876543210"
        val result = formatter.format(rawInput)
        assertEquals(rawInput, result)

        unmockkAll()
        every {
            mockContext.getSystemService(Context.TELEPHONY_SERVICE)
        } returns mockTelephonyManager
    }
}
