package com.app.dialer.presentation.theme

import android.util.Log
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import com.app.dialer.R

/**
 * Google Fonts provider backed by GMS.
 *
 * On devices without Google Play Services (e.g., AOSP emulators, custom ROMs)
 * this provider will throw at startup. The safe initialisation below catches
 * that case and falls back to the system sans-serif font families, which are
 * visually similar to Inter and Outfit on most Android builds.
 */
private val googleFontProvider: GoogleFont.Provider? = try {
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
} catch (e: Exception) {
    Log.w("DialerTypography", "Google Fonts provider unavailable — using system fallback fonts.", e)
    null
}

private val interFont = GoogleFont("Inter")
private val outfitFont = GoogleFont("Outfit")

/**
 * Inter font family for body and label text.
 * Falls back to [FontFamily.SansSerif] when GMS is unavailable.
 */
val InterFontFamily: FontFamily = if (googleFontProvider != null) {
    FontFamily(
        Font(googleFont = interFont, fontProvider = googleFontProvider, weight = FontWeight.Light),
        Font(googleFont = interFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
        Font(googleFont = interFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
        Font(googleFont = interFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
        Font(googleFont = interFont, fontProvider = googleFontProvider, weight = FontWeight.Bold),
    )
} else {
    FontFamily.SansSerif
}

/**
 * Outfit font family for display and headline text.
 * Falls back to [FontFamily.SansSerif] when GMS is unavailable.
 */
val OutfitFontFamily: FontFamily = if (googleFontProvider != null) {
    FontFamily(
        Font(googleFont = outfitFont, fontProvider = googleFontProvider, weight = FontWeight.Light),
        Font(googleFont = outfitFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
        Font(googleFont = outfitFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
        Font(googleFont = outfitFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
        Font(googleFont = outfitFont, fontProvider = googleFontProvider, weight = FontWeight.Bold),
        Font(googleFont = outfitFont, fontProvider = googleFontProvider, weight = FontWeight.ExtraBold),
    )
} else {
    FontFamily.SansSerif
}

val DialerTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
