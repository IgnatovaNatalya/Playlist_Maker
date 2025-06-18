package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.PlaylistUiState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private var playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistUiState = MutableLiveData<PlaylistUiState>()
    val playlistUiState: LiveData<PlaylistUiState> = _playlistUiState

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> =_playlistTracks


    fun getPlaylist(playlistId: Int) {
        _playlistUiState.postValue(PlaylistUiState.Loading)
        viewModelScope.launch {
            playlistsInteractor.getPlaylist(playlistId).collect { playlist ->
                _playlistUiState.postValue(PlaylistUiState.Content(playlist))
            }
        }
        viewModelScope.launch {
            playlistsInteractor.getPlaylistTracks(playlistId).collect { listTrack ->
                _playlistTracks.postValue(listTrack)
            }
        }
    }
    fun removeTrack(track:Track) {

    }
//    fun getPlaylistTracks(playlistId: Int) {
//        viewModelScope.launch {
//            playlistsInteractor.getPlaylistTracks(playlistId).collect { listTrack ->
//                _playlistTracks.postValue(listTrack)
//            }
//        }
//    }

}






