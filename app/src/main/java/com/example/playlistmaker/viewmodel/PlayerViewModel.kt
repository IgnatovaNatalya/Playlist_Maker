package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlaybackInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaybackViewModel(
    private val playbackInteractor: PlaybackInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private var playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    private val _favoriteState = MutableLiveData<Boolean>()
    var favoriteState: LiveData<Boolean> = _favoriteState

    private val _playlists = MutableLiveData<List<Playlist>>()
    var playlists: LiveData<List<Playlist>> = _playlists

    private val _toastState = MutableLiveData<String>()
    var toastState: LiveData<String> = _toastState

    private var timerJob: Job? = null

    private var currentTrack: Track? = null

    init {
        getPlaylists()
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                _playlists.postValue(playlists)
            }
        }
    }
    
    private fun startPlayer() {
        playbackInteractor.startPlayer()
        _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        playbackInteractor.pausePlayer()
        timerJob?.cancel()
        _playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    fun releasePlayer() {
        playbackInteractor.releasePlayer()
        _playerState.value = PlayerState.Default()
    }

    fun preparePlayer(track: Track) {
        currentTrack = track
        playbackInteractor.preparePlayer(
            track.previewUrl,
            { _playerState.postValue(PlayerState.Prepared()) },
            { _playerState.postValue(PlayerState.Prepared()) }
        )
        _favoriteState.postValue(track.isFavorite)
    }

    fun onLikeClicked() {
        if (currentTrack != null) {
            val track = currentTrack!!
            viewModelScope.launch {
                if (track.isFavorite) {
                    favoritesInteractor.removeFromFavorites(track)
                    track.isFavorite = false
                }
                else {
                    favoritesInteractor.addToFavorites(track)
                    track.isFavorite = true
                }
            }
            _favoriteState.postValue(track.isFavorite == false)
            currentTrack = track
        }
    }

    fun onPlayButtonClicked() {

        when (_playerState.value) {

            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> {}
        }
    }

    fun addTrackTo(playList: Playlist) {
        if (currentTrack != null) {
            val track = currentTrack!!
            viewModelScope.launch {
                playlistsInteractor.addToPlaylist(playList.id, track)
                _toastState.postValue("Добавлено в плейлист ${playList.title}")
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (playbackInteractor.isPlaying()) {
                _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(playbackInteractor.getCurrentPosition()) ?: "00:00"
    }


    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        const val TIMER_UPDATE_INTERVAL = 300L
    }
}