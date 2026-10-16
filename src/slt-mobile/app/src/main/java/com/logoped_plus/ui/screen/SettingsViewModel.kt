package com.logoped_plus.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.data.preferences.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val hideWeekend: Boolean = false,
    val gridStartHour: Int = 0,
    val gridEndHour: Int = 24
)

class SettingsViewModel(private val settings: SettingsStore) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsUiState(
        hideWeekend = settings.hideWeekend.value,
        gridStartHour = settings.gridStartHour.value,
        gridEndHour = settings.gridEndHour.value
    ))
    val uiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            settings.hideWeekend.collect { hidden -> mutableState.update { it.copy(hideWeekend = hidden) } }
        }
        viewModelScope.launch {
            settings.gridStartHour.collect { hour -> mutableState.update { it.copy(gridStartHour = hour) } }
        }
        viewModelScope.launch {
            settings.gridEndHour.collect { hour -> mutableState.update { it.copy(gridEndHour = hour) } }
        }
    }

    fun setHideWeekend(value: Boolean) {
        settings.setHideWeekend(value)
    }

    fun setGridStartHour(value: Int) {
        settings.setGridStartHour(value)
    }

    fun setGridEndHour(value: Int) {
        settings.setGridEndHour(value)
    }
}
