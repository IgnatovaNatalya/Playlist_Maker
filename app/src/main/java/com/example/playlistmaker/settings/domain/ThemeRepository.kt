package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    fun isSaved():Boolean
    fun getSavedTheme():Boolean
    fun saveTheme(theme:Boolean)
    fun switchTheme(theme: Boolean)
}