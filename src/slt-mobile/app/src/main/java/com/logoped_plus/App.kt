package com.logoped_plus

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logoped_plus.data.repository.InMemoryChildRepository
import com.logoped_plus.data.repository.InMemoryLessonRepository
import com.logoped_plus.ui.AppDrawer
import com.logoped_plus.ui.AppScreen
import com.logoped_plus.ui.screen.ChildrenScreen
import com.logoped_plus.ui.screen.ScheduleScreen
import com.logoped_plus.ui.screen.SettingsScreen
import com.logoped_plus.ui.screen.children.ChildrenViewModel
import com.logoped_plus.ui.screen.children.ChildrenViewModelFactory
import com.logoped_plus.ui.screen.schedule.ScheduleViewModel
import com.logoped_plus.ui.screen.schedule.ScheduleViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.Schedule) }
    var showExitConfirmation by remember {
        mutableStateOf(false)
    }

    BackHandler(
        enabled = currentScreen != AppScreen.Schedule
    ) {
        currentScreen = AppScreen.Schedule
    }
    BackHandler(
        enabled = currentScreen == AppScreen.Schedule
    ) {
        showExitConfirmation = true
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val lessonRepository = remember {
        InMemoryLessonRepository()
    }

    val childRepository = remember {
        InMemoryChildRepository()
    }

    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(
            lessonRepository = lessonRepository,
            childRepository = childRepository
        )
    )

    val childrenViewModel: ChildrenViewModel = viewModel(
        factory = ChildrenViewModelFactory(
            childRepository = childRepository
        )
    )

    val screenTitle = when (currentScreen) {
        AppScreen.Schedule -> "Расписание"
        AppScreen.Children -> "Дети"
        AppScreen.Settings -> "Настройки"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    currentScreen = screen

                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Открыть меню"
                            )
                        }
                    },
                    title = {
                        Text(screenTitle)
                    }
                )
            }
        ) { innerPadding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.Schedule -> ScheduleScreen(
                        viewModel = scheduleViewModel
                    )

                    AppScreen.Children -> ChildrenScreen(
                        viewModel = childrenViewModel
                    )

                    AppScreen.Settings -> SettingsScreen()
                }
            }
        }
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showExitConfirmation = false
            },
            title = {
                Text("Выйти из приложения?")
            },
            text = {
                Text("Вы действительно хотите выйти?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitConfirmation = false
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text("Выйти")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitConfirmation = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}