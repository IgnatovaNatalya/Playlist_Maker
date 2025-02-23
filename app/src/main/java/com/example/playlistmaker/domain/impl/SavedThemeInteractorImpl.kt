package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactor.SavedThemeInteractor
import com.example.playlistmaker.domain.repository.PreferencesRepository

class SavedThemeInteractorImpl (private val repository: PreferencesRepository):SavedThemeInteractor {
    override fun getSavedTheme(): Boolean {
        return repository.getSavedTheme()
    }

    override fun setTheme(theme: Boolean) {
        repository.setTheme(theme)
    }

}