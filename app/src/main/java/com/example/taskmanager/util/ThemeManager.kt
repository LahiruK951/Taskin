package com.example.taskmanager.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREF_NAME = "theme_pref"
    private const val KEY_DARK_MODE = "dark_mode"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isDarkMode(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_DARK_MODE, false)
    }

    fun saveThemeMode(context: Context, isDarkMode: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
    }

    fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}