package com.example.playlistmaker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.player.PlaybackInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaybackViewModel(
    private val playbackInteractor: PlaybackInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    private val _favoriteState = MutableLiveData<Boolean>()
    var favoriteState: LiveData<Boolean> = _favoriteState

    private var timerJob: Job? = null

    private var currentTrack: Track? = null

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
    }

    fun onLikeClicked() {
        if (currentTrack != null) {
            val track = currentTrack!!
            viewModelScope.launch {
                if (track.isFavorite) favoritesInteractor.removeFromFavorites(track)
                else favoritesInteractor.addToFavorites(track)
            }
            _favoriteState.postValue(!track.isFavorite)
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