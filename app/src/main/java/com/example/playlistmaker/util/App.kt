package com.example.playlistmaker.util

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.di.mediaModule
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchTracksModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.domain.settings.ThemeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class App : Application() {

    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidContext(this@App)
            modules(
                searchTracksModule,
                settingsModule,
                playerModule,
                mediaModule,
                dataModule
            )
        }

        if (themeInteractor.isSaved()) {
            themeInteractor.switchTheme(themeInteractor.getSavedTheme())
        } else {
            val systemNightIsOn =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            themeInteractor.saveTheme(systemNightIsOn)
        }
    }
}