package com.app.dialer.presentation.dialer.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Top bar for the Dialer screen.
 *
 * Uses Material3 [CenterAlignedTopAppBar] with a transparent background so the
 * neumorphic body below shows through. Status-bar insets are handled by the
 * [TopAppBarDefaults.windowInsets] parameter.
 *
 * @param onOpenSettings  Called when the user taps "Settings" in the overflow menu.
 * @param onOpenSearch    Called when the user taps the search icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerTopBar(
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var overflowExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Dialer",
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            // Search
            IconButton(onClick = onOpenSearch) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search contacts"
                )
            }

            // Overflow menu
            IconButton(onClick = { overflowExpanded = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More options"
                )
            }
            DropdownMenu(
                expanded = overflowExpanded,
                onDismissRequest = { overflowExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        overflowExpanded = false
                        onOpenSettings()
                    }
                )
                DropdownMenuItem(
                    text = { Text("About") },
                    onClick = { overflowExpanded = false }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        windowInsets = WindowInsets.statusBars,
        modifier = modifier
    )
}
