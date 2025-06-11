package com.example.playlistmaker.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.internalStorage.InternalStorageInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val internalStorageInteractor: InternalStorageInteractor
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

    fun saveImage(uri:Uri) {
        viewModelScope.launch {
            internalStorageInteractor.saveImage(uri)
        }
    }

    companion object {
        private const val CREATE_DEBOUNCE_DELAY = 2000L
    }

}