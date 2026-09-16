package com.logoped_plus.ui

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.disabled

@Composable
fun AppDrawer(
    currentScreen: AppScreen,
    enabled: Boolean = true,
    onScreenSelected: (AppScreen) -> Unit
) {
    ModalDrawerSheet {
        Text("Логопед+")

        NavigationDrawerItem(
            modifier = Modifier.alpha(if (enabled) 1f else 0.38f).semantics { if (!enabled) disabled() },
            label = {
                Text("Расписание")
            },
            selected = currentScreen == AppScreen.Schedule,
            onClick = {
                if (enabled) onScreenSelected(AppScreen.Schedule)
            }
        )

        NavigationDrawerItem(
            modifier = Modifier.alpha(if (enabled) 1f else 0.38f).semantics { if (!enabled) disabled() },
            label = {
                Text("Дети")
            },
            selected = currentScreen == AppScreen.Children,
            onClick = {
                if (enabled) onScreenSelected(AppScreen.Children)
            }
        )

        NavigationDrawerItem(
            modifier = Modifier.alpha(if (enabled) 1f else 0.38f).semantics { if (!enabled) disabled() },
            label = {
                Text("Настройки")
            },
            selected = currentScreen == AppScreen.Settings,
            onClick = {
                if (enabled) onScreenSelected(AppScreen.Settings)
            }
        )
    }
}