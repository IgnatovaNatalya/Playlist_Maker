package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private val _playlistsState = MutableLiveData<PlaylistsState>()
    val playlistsState: LiveData<PlaylistsState> = _playlistsState

    init {
        getPlaylists()
    }
    fun getPlaylists() {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) renderState(PlaylistsState.Empty)
                else renderState(PlaylistsState.Content(playlists = playlists))
            }
        }
    }

    private fun renderState(state: PlaylistsState) {
        _playlistsState.postValue(state)
    }

}