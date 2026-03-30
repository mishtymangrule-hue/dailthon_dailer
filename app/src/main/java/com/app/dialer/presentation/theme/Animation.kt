package com.app.dialer.presentation.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally

/**
 * Centralised animation specifications for the Dialer UI.
 *
 * All motion in the keypad screen references these specs so that timing can
 * be tuned globally without hunting through individual composables.
 */
object DialerAnimations {

    /**
     * Spring used for keypad button press / release scale animation.
     * Low damping ratio produces a slight bounce on release — reinforces
     * the neumorphic "physical button" metaphor.
     */
    val keypadButtonPress: SpringSpec<Float> = spring(
        dampingRatio = 0.6f,
        stiffness = Spring.StiffnessHigh
    )

    /**
     * Tween for contact suggestion items sliding into the list.
     */
    val suggestionSlideIn: TweenSpec<Float> = tween(
        durationMillis = 200,
        easing = FastOutSlowInEasing
    )

    /**
     * Infinite animation spec for the call button pulse.
     * Scales 1.0 → 1.06 → 1.0 over 1 200 ms.
     */
    val callButtonPulse = infiniteRepeatable(
        animation = tween<Float>(durationMillis = 1200, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )

    /**
     * Fast tween for the input field digit slide-in.
     */
    val inputSlide: TweenSpec<Float> = tween(
        durationMillis = 80,
        easing = LinearOutSlowInEasing
    )

    /**
     * Combined enter transition for dialogs / overlays.
     */
    val dialogEnter: EnterTransition =
        fadeIn(animationSpec = tween(220)) +
        scaleIn(initialScale = 0.9f, animationSpec = tween(220))

    /**
     * Combined exit transition for dialogs / overlays.
     */
    val dialogExit: ExitTransition =
        fadeOut(animationSpec = tween(180)) +
        scaleOut(targetScale = 0.9f, animationSpec = tween(180))

    /**
     * Enter transition for each suggestion list item.
     * The [offsetX] lambda is called with the full width and should be negated
     * to slide in from the right.
     */
    fun suggestionItemEnter(offsetX: (Int) -> Int = { it }): EnterTransition =
        fadeIn(animationSpec = tween(200)) +
        slideInHorizontally(
            initialOffsetX = offsetX,
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        )
}
