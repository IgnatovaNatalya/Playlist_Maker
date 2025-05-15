package com.example.playlistmaker.util

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.di.MediaKoinModule
import com.example.playlistmaker.di.PlayerKoinModule
import com.example.playlistmaker.di.SearchKoinModule
import com.example.playlistmaker.di.SettingsKoinModule
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
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
                SearchKoinModule,
                SettingsKoinModule,
                PlayerKoinModule,
                MediaKoinModule,
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