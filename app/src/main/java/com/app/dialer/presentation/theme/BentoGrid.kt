package com.app.dialer.presentation.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Receiver scope for [BentoGrid] — provides [item] DSL for placing
 * variable-span bento cards inside the grid.
 *
 * Row span is simulated by fixing the item height to [cellHeight] * [rowSpan].
 * Column span is delegated to [LazyVerticalGrid]'s native [GridItemSpan].
 */
class BentoGridScope(
    private val lazyGridScope: LazyGridScope,
    private val totalColumns: Int,
    private val cellHeight: Dp
) {
    /**
     * Place one bento item that spans [colSpan] columns and has a height
     * proportional to [rowSpan] * [cellHeight].
     *
     * @param colSpan   Number of grid columns to span (clamped to [totalColumns]).
     * @param rowSpan   Height multiplier — item height = [cellHeight] * [rowSpan].
     * @param content   The composable content rendered inside this bento slot.
     */
    fun item(
        colSpan: Int = 1,
        rowSpan: Int = 1,
        content: @Composable BoxScope.() -> Unit
    ) {
        val clampedColSpan = colSpan.coerceIn(1, totalColumns)
        val itemHeight = cellHeight * rowSpan

        lazyGridScope.item(
            span = { GridItemSpan(clampedColSpan) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                content = content
            )
        }
    }
}

/**
 * A modular Bento-style grid layout built on [LazyVerticalGrid].
 *
 * Provides a [BentoGridScope] DSL where each item can declare a [colSpan]
 * and [rowSpan], enabling varied card sizes within a fixed column count.
 *
 * @param columns      Number of equal-width columns in the grid.
 * @param modifier     Modifier applied to the outer [LazyVerticalGrid].
 * @param cellHeight   Base height for one bento row unit. Default 100.dp.
 * @param contentPadding Padding around the grid content.
 * @param content      DSL block accepting [BentoGridScope].
 */
@Composable
fun BentoGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    cellHeight: Dp = 100.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: BentoGridScope.() -> Unit
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding
    ) {
        BentoGridScope(
            lazyGridScope = this,
            totalColumns = columns,
            cellHeight = cellHeight
        ).content()
    }
}
