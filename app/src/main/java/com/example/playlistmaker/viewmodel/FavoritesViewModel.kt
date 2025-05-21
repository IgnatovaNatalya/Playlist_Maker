package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.util.FavoritesState
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _favState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> = _favState

    init {
        getFavorites()
    }

    fun getFavorites() {
        renderState(FavoritesState.Loading)

        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { favoriteTracks ->
                if (favoriteTracks.isEmpty()) renderState(FavoritesState.Empty)
                else renderState(FavoritesState.Content(favoriteTracks = favoriteTracks))
            }
        }
    }

    private fun renderState(state: FavoritesState) {
        _favState.postValue(state)
    }

}