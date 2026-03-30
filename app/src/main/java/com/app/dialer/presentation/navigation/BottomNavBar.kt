package com.app.dialer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.dialer.presentation.theme.ElectricBlue
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * Data class representing a bottom navigation destination.
 */
private data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(
        label = "Dialer",
        route = NavRoutes.Dialer.route,
        selectedIcon = Icons.Filled.Call,
        unselectedIcon = Icons.Outlined.Call
    ),
    BottomNavItem(
        label = "Contacts",
        route = NavRoutes.Contacts.route,
        selectedIcon = Icons.Filled.Contacts,
        unselectedIcon = Icons.Outlined.Contacts
    ),
    BottomNavItem(
        label = "Recents",
        route = NavRoutes.CallLog.route,
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    ),
    BottomNavItem(
        label = "Settings",
        route = NavRoutes.Settings.route,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

/**
 * Material3 [NavigationBar] with neumorphic surface effect applied to the container.
 *
 * Navigation state is driven by [currentRoute]. Tapping an item performs a
 * single-top navigate that saves and restores state for each destination.
 *
 * @param navController  Controller used to navigate on item tap.
 * @param currentRoute   Currently active back-stack route, used for selection state.
 * @param modifier       Optional modifier applied to the [NavigationBar].
 */
@Composable
fun BottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.neumorphicSurface(
            cornerRadius = 0.dp,
            elevation = 8.dp
        ),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            // Pop back to the start destination to avoid a large back stack
                            popUpTo(NavRoutes.Dialer.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricBlue,
                    selectedTextColor = ElectricBlue,
                    indicatorColor = ElectricBlue.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
