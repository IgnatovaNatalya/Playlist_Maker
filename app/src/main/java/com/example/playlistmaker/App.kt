package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.interactor.ThemeInteractor

class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        themeInteractor = Creator.provideThemeInteractor(this)

        if (themeInteractor.isSaved()) {
            themeInteractor.switchTheme(themeInteractor.getSavedTheme())
        } else {
            val systemNightIsOn =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            themeInteractor.saveTheme(systemNightIsOn)
        }
    }
}
