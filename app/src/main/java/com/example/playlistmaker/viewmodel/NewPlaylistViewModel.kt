package com.example.playlistmaker.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.internalStorage.InternalStorageInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val internalStorageInteractor: InternalStorageInteractor
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<Boolean>()
    val playlistCreated: LiveData<Boolean> =_playlistCreated

    fun createPlaylist(title: String, description: String, path: String) {
        _playlistCreated.postValue(false)
        viewModelScope.launch {
            playlistsInteractor.createPlaylist(title, description, path)
            _playlistCreated.postValue(true)
        }
    }

    fun saveImage(uri:Uri) {
        viewModelScope.launch {
            internalStorageInteractor.saveImage(uri)
        }
    }
}