package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.domain.ThemeInteractor

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