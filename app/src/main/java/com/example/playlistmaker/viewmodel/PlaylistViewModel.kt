package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.util.PlaylistUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val shareInteractor: SharingInteractor
) : ViewModel() {

    private val _playlistUiState = MutableLiveData<PlaylistUiState>()
    val playlistUiState: LiveData<PlaylistUiState> = _playlistUiState

    private val _toastState = MutableLiveData<String>()
    val toastState: LiveData<String> = _toastState

    init {
        viewModelScope.launch {
            getPlaylistAndTracks()
        }
    }

    fun removeTrack(track: Track) {
        val state = _playlistUiState.value
        if (state is PlaylistUiState.Content)
            viewModelScope.launch {
                playlistsInteractor.removeTrackFromPlaylist(track, playlistId)
                getPlaylistAndTracks()
            }
    }

    fun deletePlaylist() {
        val state = _playlistUiState.value
        if (state is PlaylistUiState.Content)
            viewModelScope.launch {
                playlistsInteractor.deletePlaylist(state.playlist)
            }
    }

    fun sharePlaylist() {

        val state = _playlistUiState.value

        if (state is PlaylistUiState.Content) {
            val tracks = state.tracks

            if (tracks.isEmpty())
                _toastState.postValue("В этом плейлисте нет списка треков, которым можно поделиться")
            else
                shareInteractor.sharePlaylist(state.playlist, tracks)
        }
    }

    private fun getPlaylistAndTracks() {
        _playlistUiState.postValue(PlaylistUiState.Loading)

        viewModelScope.launch {
            val playlist = async { playlistsInteractor.getPlaylist(playlistId) }.await()
            val tracks = async { playlistsInteractor.getPlaylistTracks(playlistId) }.await()

            _playlistUiState.postValue(
                PlaylistUiState.Content(
                    playlist.first(),
                    tracks.first()
                )
            )
        }
    }

    fun reloadData() {
        viewModelScope.launch {
            getPlaylistAndTracks()
        }
    }

    private fun clearToast() {
        _toastState.postValue("")
    }

    fun onPause() {
        clearToast()
    }
}






