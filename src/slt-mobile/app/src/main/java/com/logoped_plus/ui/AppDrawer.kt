package com.logoped_plus.ui

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppDrawer(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    ModalDrawerSheet {
        Text("Логопед+")

        NavigationDrawerItem(
            label = {
                Text("Расписание")
            },
            selected = currentScreen == AppScreen.Schedule,
            onClick = {
                onScreenSelected(AppScreen.Schedule)
            }
        )

        NavigationDrawerItem(
            label = {
                Text("Дети")
            },
            selected = currentScreen == AppScreen.Children,
            onClick = {
                onScreenSelected(AppScreen.Children)
            }
        )

        NavigationDrawerItem(
            label = {
                Text("Настройки")
            },
            selected = currentScreen == AppScreen.Settings,
            onClick = {
                onScreenSelected(AppScreen.Settings)
            }
        )
    }
}