package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_PREFS = "playlist_maker_prefs"
const val DARK_THEME = "dark_theme"

class App : Application() {

    var darkTheme = false
        private set

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFS, MODE_PRIVATE)

        if (sharedPreferences.contains(DARK_THEME)) {
            val savedTheme = sharedPreferences.getBoolean(DARK_THEME, false)
            switchTheme(savedTheme)
        }
        else {
            val nightIsOn = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(
                if (nightIsOn)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            })

        sharedPreferences.edit()
            .putBoolean(DARK_THEME, darkTheme)
            .apply()
    }
}