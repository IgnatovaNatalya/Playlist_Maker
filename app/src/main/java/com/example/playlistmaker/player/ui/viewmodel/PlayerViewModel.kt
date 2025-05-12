package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlaybackInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaybackViewModel(private val playbackInteractor: PlaybackInteractor) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    private var timerJob: Job? = null

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
        playbackInteractor.preparePlayer(
            track.previewUrl,
            { _playerState.postValue(PlayerState.Prepared()) },
            { _playerState.postValue(PlayerState.Prepared()) }
        )
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
        timerJob = viewModelScope.launch {
            while (playbackInteractor.isPlaying()) {
                delay(300L)
                _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
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
}