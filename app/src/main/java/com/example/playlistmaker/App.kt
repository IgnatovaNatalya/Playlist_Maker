package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.interactor.SavedThemeInteractor


class App : Application() {

    var darkTheme = false
        private set

    private lateinit var themeInteractor: SavedThemeInteractor

    override fun onCreate() {
        super.onCreate()

        themeInteractor = Creator.provideThemeInteractor(this)
        val savedTheme = themeInteractor.getSavedTheme()

        if (savedTheme) {
            darkTheme = true
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

        themeInteractor.setTheme(darkTheme)
    }
}