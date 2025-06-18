package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.util.PlaylistUiState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val shareInteractor: SharingInteractor
) : ViewModel() {

    private val _playlistUiState = MutableLiveData<PlaylistUiState>()
    val playlistUiState: LiveData<PlaylistUiState> = _playlistUiState

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    private val _toastState = MutableLiveData<String>()
    val toastState: LiveData<String> = _toastState

    init {
        getPlaylist()
        getListTrack()
    }

    fun removeTrack(track: Track) {
        val state = _playlistUiState.value
        if (state is PlaylistUiState.Content)
            viewModelScope.launch {
                playlistsInteractor.removeTrackFromPlaylist(track, playlistId)
            }
    }

    fun deletePlaylist() {
        val state = _playlistUiState.value
        if (state is PlaylistUiState.Content)
            viewModelScope.launch {
                playlistsInteractor.deletePlaylist(state.playlist)
                _playlistUiState.value  = PlaylistUiState.Empty
            }
    }

    fun sharePlaylist() {
        val state = _playlistUiState.value
        val tracks = _playlistTracks.value

        if (tracks.isNullOrEmpty()) {
            _toastState.postValue("В этом плейлисте нет списка треков, которым можно поделиться")
            return
        }

        if (state !is PlaylistUiState.Content) {
            return
        }

        val playlist = state.playlist
        shareInteractor.sharePlaylist(playlist, tracks)
    }

    private fun getPlaylist() {
        _playlistUiState.postValue(PlaylistUiState.Loading)
        viewModelScope.launch {
            playlistsInteractor.getPlaylist(playlistId).collect { playlist ->
                _playlistUiState.postValue(PlaylistUiState.Content(playlist))
            }
        }
    }

    private fun getListTrack() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistTracks(playlistId).collect { listTrack ->
                _playlistTracks.value = listTrack
            }
        }
    }

    private fun clearToast() {
        _toastState.postValue("")
    }

    fun onPause() {
        clearToast()
    }
}






