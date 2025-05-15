package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.data.settings.ThemeRepository

class ThemeInteractorImpl (private val repository: ThemeRepository): ThemeInteractor {

    override fun isSaved(): Boolean {
        return repository.isSaved()
    }

    override fun getSavedTheme(): Boolean {
        return repository.getSavedTheme()
    }

    override fun saveTheme(theme: Boolean) {
        repository.saveTheme(theme)
    }

    override fun switchTheme(theme: Boolean) {
        repository.switchTheme(theme)
        saveTheme(theme)
    }
}