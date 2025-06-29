package com.example.playlistmaker.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.internalStorage.InternalStorageInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.NewPlaylistUiState
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistId: Int?,
    private val playlistsInteractor: PlaylistsInteractor,
    private val internalStorageInteractor: InternalStorageInteractor
) : ViewModel() {

    private val _newPlaylistsState = MutableLiveData<NewPlaylistUiState>()
    val newPlaylistsState: LiveData<NewPlaylistUiState> = _newPlaylistsState

    private val _playlistSaved = MutableLiveData<Boolean>()
    val playlistCreated: LiveData<Boolean> = _playlistSaved

    init {
        getPlaylist()
    }

    fun createPlaylist(title: String, description: String, path: String) {
        _playlistSaved.postValue(false)
        viewModelScope.launch {
            playlistsInteractor.createPlaylist(playlistId, title, description, path)
            _playlistSaved.postValue(true)
        }
    }

    fun saveImage(uri:Uri) {
        viewModelScope.launch {
            internalStorageInteractor.saveImage(uri)
        }
    }


    private fun getPlaylist() {
        if (playlistId != null)
            viewModelScope.launch {
                playlistsInteractor.getPlaylist(playlistId).collect { playlist ->
                    _newPlaylistsState.postValue(NewPlaylistUiState.Exist(playlist))
                }
            }
    }


}