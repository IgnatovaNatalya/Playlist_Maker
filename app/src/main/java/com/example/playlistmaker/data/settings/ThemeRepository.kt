package com.example.playlistmaker.data.settings

interface ThemeRepository {
    fun isSaved():Boolean
    fun getSavedTheme():Boolean
    fun saveTheme(theme:Boolean)
    fun switchTheme(theme: Boolean)
}