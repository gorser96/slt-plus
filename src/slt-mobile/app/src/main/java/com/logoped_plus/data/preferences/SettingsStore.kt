package com.logoped_plus.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsStore(context: Context) {
    private val preferences: SharedPreferences = context.applicationContext
        .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    private val mutableHideWeekend = MutableStateFlow(
        preferences.getBoolean(KEY_HIDE_WEEKEND, DEFAULT_HIDE_WEEKEND)
    )
    val hideWeekend: StateFlow<Boolean> = mutableHideWeekend.asStateFlow()

    fun setHideWeekend(value: Boolean) {
        preferences.edit().putBoolean(KEY_HIDE_WEEKEND, value).apply()
        mutableHideWeekend.value = value
    }

    private companion object {
        const val PREFS_FILE = "settings"
        const val KEY_HIDE_WEEKEND = "hide_weekend"
        const val DEFAULT_HIDE_WEEKEND = false
    }
}
