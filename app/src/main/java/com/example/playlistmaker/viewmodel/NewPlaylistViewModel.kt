package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _buttonState = MutableLiveData<Boolean>()
    val buttonState: LiveData<Boolean> = _buttonState

    private val createPlaylistDebounce = debounce<Playlist>(
        CREATE_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { playlist -> createPlaylist(playlist) }

    fun createDebounce(playlist: Playlist) {
        createPlaylistDebounce(playlist)
    }

    fun createPlaylist(playlist: Playlist) {
        _buttonState.postValue(false)

        viewModelScope.launch {
            playlistsInteractor.createPlaylist(playlist)
            _buttonState.postValue(true)
        }
    }

    companion object {
        private const val CREATE_DEBOUNCE_DELAY = 2000L
    }

}