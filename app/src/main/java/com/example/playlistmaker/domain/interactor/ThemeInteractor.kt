package com.example.playlistmaker.domain.interactor

interface ThemeInteractor {
    fun isSaved():Boolean
    fun getSavedTheme():Boolean
    fun saveTheme(theme:Boolean)
    fun switchTheme(theme: Boolean)
}