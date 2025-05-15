package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeRepositoryImpl (private val sharedPrefs: SharedPreferences) : ThemeRepository {

    companion object {
        const val DARK_THEME = "dark_theme"
    }

    override fun isSaved(): Boolean {
        return sharedPrefs.contains(DARK_THEME)
    }

    override fun getSavedTheme():Boolean {
        return sharedPrefs.getBoolean(DARK_THEME, false)
    }

    override fun saveTheme(theme: Boolean) {
        sharedPrefs.edit() {
            putBoolean(DARK_THEME, theme)
        }
    }

    override fun switchTheme(theme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}