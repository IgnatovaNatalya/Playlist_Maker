package com.example.playlistmaker.settings.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val sharingInteractor = Creator.provideSharingInteractor(getApplication())
    private val themeInteractor = Creator.provideThemeInteractor(getApplication())

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    fun changeTheme(theme: Boolean) {
        themeInteractor.switchTheme(theme)
        _themeState.postValue(theme)
    }

    fun getSavedTheme() {
        _themeState.postValue(
            themeInteractor.getSavedTheme()
        )
    }
}