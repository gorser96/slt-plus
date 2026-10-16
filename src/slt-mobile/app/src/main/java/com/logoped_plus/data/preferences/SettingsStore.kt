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
    private val mutableGridStartHour = MutableStateFlow(
        preferences.getInt(KEY_GRID_START_HOUR, DEFAULT_GRID_START_HOUR)
    )
    val gridStartHour: StateFlow<Int> = mutableGridStartHour.asStateFlow()
    private val mutableGridEndHour = MutableStateFlow(
        preferences.getInt(KEY_GRID_END_HOUR, DEFAULT_GRID_END_HOUR)
    )
    val gridEndHour: StateFlow<Int> = mutableGridEndHour.asStateFlow()
    private val mutableVisibleTagsLimit = MutableStateFlow(
        preferences.getInt(KEY_VISIBLE_TAGS_LIMIT, DEFAULT_VISIBLE_TAGS_LIMIT)
    )
    val visibleTagsLimit: StateFlow<Int> = mutableVisibleTagsLimit.asStateFlow()

    fun setHideWeekend(value: Boolean) {
        preferences.edit().putBoolean(KEY_HIDE_WEEKEND, value).apply()
        mutableHideWeekend.value = value
    }

    fun setGridStartHour(value: Int) {
        preferences.edit().putInt(KEY_GRID_START_HOUR, value).apply()
        mutableGridStartHour.value = value
    }

    fun setGridEndHour(value: Int) {
        preferences.edit().putInt(KEY_GRID_END_HOUR, value).apply()
        mutableGridEndHour.value = value
    }

    fun setVisibleTagsLimit(value: Int) {
        preferences.edit().putInt(KEY_VISIBLE_TAGS_LIMIT, value).apply()
        mutableVisibleTagsLimit.value = value
    }

    private companion object {
        const val PREFS_FILE = "settings"
        const val KEY_HIDE_WEEKEND = "hide_weekend"
        const val DEFAULT_HIDE_WEEKEND = false
        const val KEY_GRID_START_HOUR = "grid_start_hour"
        const val DEFAULT_GRID_START_HOUR = 0
        const val KEY_GRID_END_HOUR = "grid_end_hour"
        const val DEFAULT_GRID_END_HOUR = 24
        const val KEY_VISIBLE_TAGS_LIMIT = "visible_tags_limit"
        const val DEFAULT_VISIBLE_TAGS_LIMIT = 3
    }
}
