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
import com.example.playlistmaker.util.AddToPlaylistResult
import com.example.playlistmaker.util.PlayerState
import com.example.playlistmaker.util.PlayerUiState
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


    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _toastState = MutableLiveData<String>()
    val toastState: LiveData<String> = _toastState

    private val _playerUiState = MutableLiveData<PlayerUiState>()
    val playerUiState: LiveData<PlayerUiState> = _playerUiState

    private var timerJob: Job? = null

    init {
        getPlaylists()
    }

    fun setTrack(track: Track) {
        _playerUiState.postValue(
            PlayerUiState(
                track = track,
                playerState = PlayerState.Default(),
                isFavorite = track.isFavorite
            )
        )
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                _playlists.postValue(playlists)
            }
        }
    }

    private fun startPlayer() {
        val track = _playerUiState.value?.track
        if (track != null) {
            playbackInteractor.startPlayer()
            _playerUiState.postValue(
                _playerUiState.value?.copy(
                    playerState = PlayerState.Playing(
                        getCurrentPlayerPosition()
                    )
                )
            )
            startTimer()
        }
    }

    private fun pausePlayer() {
        val track = _playerUiState.value?.track
        if (track != null) {
            playbackInteractor.pausePlayer()
            timerJob?.cancel()
            _playerUiState.postValue(
                _playerUiState.value?.copy(
                    playerState = PlayerState.Paused(
                        getCurrentPlayerPosition()
                    )
                )
            )
        }
    }

    fun releasePlayer() {
        val track = _playerUiState.value?.track
        if (track != null) {
            playbackInteractor.releasePlayer()
            _playerUiState.postValue(_playerUiState.value?.copy(playerState = PlayerState.Default()))
        }
    }

    fun preparePlayer(track: Track) {
        playbackInteractor.preparePlayer(
            track.previewUrl,
            { _playerUiState.postValue(_playerUiState.value?.copy(playerState = PlayerState.Prepared())) },
            { _playerUiState.postValue(_playerUiState.value?.copy(playerState = PlayerState.Prepared())) }
        )
    }

    fun onLikeClicked() {
        val track = _playerUiState.value?.track
        if (track != null) {
            viewModelScope.launch {
                if (track.isFavorite == true)
                    favoritesInteractor.removeFromFavorites(track)
                else
                    favoritesInteractor.addToFavorites(track)
            }
            _playerUiState.postValue(
                _playerUiState.value?.copy(
                    track = track.copy(isFavorite = !track.isFavorite),
                    isFavorite = !track.isFavorite
                )
            )
        }

    }

    fun onPlayButtonClicked() {

        when (_playerUiState.value?.playerState) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun addTrackTo(playList: Playlist) {
        val track = _playerUiState.value?.track
        if (track != null) {
            viewModelScope.launch {
                when (val result = playlistsInteractor.addToPlaylist(playList.id, track)) {
                    is AddToPlaylistResult.Success -> {
                        _toastState.postValue("Добавлено в плейлист ${playList.title}")
                    }

                    is AddToPlaylistResult.AlreadyExists -> {
                        _toastState.postValue("Трек уже добавлен в плейлист ${playList.title}")
                    }

                    is AddToPlaylistResult.Error -> {
                        _toastState.postValue("Ошибка: ${result.exception.message}")
                    }
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (playbackInteractor.isPlaying()) {
                _playerUiState.postValue(
                    _playerUiState.value?.copy(
                        playerState = PlayerState.Playing(
                            getCurrentPlayerPosition()
                        )
                    )
                )
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(playbackInteractor.getCurrentPosition()) ?: ZERO_TIME
    }

    private fun clearToast() {
        _toastState.postValue("")
    }

    fun onPause() {
        pausePlayer()
        clearToast()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        const val TIMER_UPDATE_INTERVAL = 300L
        const val ZERO_TIME = "00:00"
    }
}






