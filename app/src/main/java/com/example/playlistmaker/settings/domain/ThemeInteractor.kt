package com.example.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isSaved():Boolean
    fun getSavedTheme():Boolean
    fun saveTheme(theme:Boolean)
    fun switchTheme(theme: Boolean)
}
