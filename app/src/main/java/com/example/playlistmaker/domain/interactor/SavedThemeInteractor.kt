package com.example.playlistmaker.domain.interactor

interface SavedThemeInteractor {
    fun getSavedTheme():Boolean
    fun setTheme(theme:Boolean)
}