package com.example.playlistmaker.domain.interactor

interface SavedThemeInteractor {
    fun containsTheme():Boolean
    fun getSavedTheme():Boolean
    fun saveTheme(theme:Boolean)
    fun switchTheme(theme: Boolean)
}