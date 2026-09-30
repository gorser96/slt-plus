package com.logoped_plus.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.data.preferences.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val hideWeekend: Boolean = false
)

class SettingsViewModel(private val settings: SettingsStore) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsUiState(settings.hideWeekend.value))
    val uiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            settings.hideWeekend.collect { hidden -> mutableState.update { it.copy(hideWeekend = hidden) } }
        }
    }

    fun setHideWeekend(value: Boolean) {
        settings.setHideWeekend(value)
    }
}
