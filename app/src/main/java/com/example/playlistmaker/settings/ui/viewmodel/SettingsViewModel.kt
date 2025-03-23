package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val shareInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _themeState

    fun changeTheme(theme: Boolean) {
        themeInteractor.switchTheme(theme)
        _themeState.postValue(theme)
    }

    fun getSavedTheme() {
        _themeState.postValue(
            themeInteractor.getSavedTheme()
        )
    }

    fun shareApp() {
        shareInteractor.shareApp()
    }

    fun callSupport() {
        shareInteractor.sendEmail()
    }

    fun openAgreement() {
        shareInteractor.openTerms()
    }
}