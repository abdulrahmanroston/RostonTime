package com.rostontime

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("roston_time_prefs", Context.MODE_PRIVATE)

    fun getCustomPhrases(): List<String> {
        val phrasesSet = prefs.getStringSet("custom_phrases", emptySet()) ?: emptySet()
        return phrasesSet.toList()
    }

    fun setCustomPhrases(phrases: List<String>) {
        prefs.edit().putStringSet("custom_phrases", phrases.toSet()).apply()
    }

    fun isLongPressEnabled(): Boolean {
        return prefs.getBoolean("long_press_enabled", false)
    }

    fun setLongPressEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("long_press_enabled", enabled).apply()
    }

    fun getLongPressDuration(): Int {
        return prefs.getInt("long_press_duration", 1000)
    }

    fun setLongPressDuration(duration: Int) {
        prefs.edit().putInt("long_press_duration", duration).apply()
    }
}
