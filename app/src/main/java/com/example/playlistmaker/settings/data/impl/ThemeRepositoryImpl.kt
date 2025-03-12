package com.example.playlistmaker.settings.data.impl

import android.content.Context
import com.example.playlistmaker.settings.domain.ThemeRepository

const val DARK_THEME = "dark_theme"
const val PLAYLIST_MAKER_PREFS = "playlist_maker_prefs"//todo дублируется

class ThemeRepositoryImpl (context: Context) : ThemeRepository {

    private var sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFS, Context.MODE_PRIVATE)

//    override fun containsTheme(): Boolean {
//        return sharedPrefs.contains(DARK_THEME)
//    }

    override fun isSaved(): Boolean {
        return sharedPrefs.contains(DARK_THEME)
    }

    override fun getSavedTheme():Boolean {
        return sharedPrefs.getBoolean(DARK_THEME, false)
    }

    override fun saveTheme(theme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, theme)
            .apply()
    }

    override fun switchTheme(theme: Boolean) {
        TODO("Not yet implemented")
    }

}