package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.interactor.SavedThemeInteractor
import com.example.playlistmaker.domain.repository.PreferencesRepository

class SavedThemeInteractorImpl (private val repository: PreferencesRepository):SavedThemeInteractor {

    override fun containsTheme(): Boolean {
        return repository.containsTheme()
    }

    override fun getSavedTheme(): Boolean {
        return repository.getSavedTheme()
    }

    override fun saveTheme(theme: Boolean) {
        repository.saveTheme(theme)
    }

    override fun switchTheme(theme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveTheme(theme)
    }
}