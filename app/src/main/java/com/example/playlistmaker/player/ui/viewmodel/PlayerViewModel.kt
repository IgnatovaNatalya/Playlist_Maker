package com.example.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlaybackInteractor

import com.example.playlistmaker.search.domain.model.Track

class PlaybackViewModel(private val playbackInteractor: PlaybackInteractor) : ViewModel() {

    companion object {
        private const val START_TIME = 1
    }


    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var time = START_TIME

    fun pausePlayer() {
        playbackInteractor.pausePlayer()
    }

    fun releasePlayer() {
        playbackInteractor.releasePlayer()
    }

    fun preparePlayer(track: Track) {
        _playerState.postValue(PlayerState.NotPrepared)
        playbackInteractor.preparePlayer(
            track.previewUrl,
            {
                resetTimer()
                _playerState.postValue(PlayerState.Prepared)

            },
            {
                resetTimer()
                _playerState.postValue(PlayerState.Completed)
            }
        )
    }

    fun playbackControl() {
        playbackInteractor.playbackControl()

        if (playbackInteractor.isPlaying()) {
            countTime()
        } else {
            _playerState.postValue(PlayerState.Paused)
            mainThreadHandler.removeCallbacksAndMessages(null)
        }
    }

    private fun resetTimer() {
        time = START_TIME
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun countTime() {
        mainThreadHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    _playerState.postValue(PlayerState.Playing(playerTime = time))
                    time++
                    mainThreadHandler.postDelayed(this, 1000L)
                }
            }, 1000L
        )
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacksAndMessages(null)
        releasePlayer()
    }
}