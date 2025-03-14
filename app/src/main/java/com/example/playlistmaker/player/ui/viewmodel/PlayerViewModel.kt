package com.example.playlistmaker.player.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator

import com.example.playlistmaker.search.domain.model.Track

class PlaybackViewModel(application: Application) :
    AndroidViewModel(application) { // todo нужен ли контекст?

    //class PlaybackViewModel: ViewModel() {

    companion object {
        private const val START_TIME = 1

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaybackViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private var playbackInteractor = Creator.providePlaybackInteractor()

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _playerTime = MutableLiveData<String>()
    val playerTime: LiveData<String> = _playerTime

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
                _playerState.postValue(PlayerState.Prepared)
                resetTimer()
            },
            {
                _playerState.postValue(PlayerState.Completed)
                resetTimer()
            }
        )
    }

    fun playbackControl() {
        playbackInteractor.playbackControl()

        if (playbackInteractor.isPlaying()) {
            _playerState.postValue(PlayerState.Playing)
            countTime()
        } else {
            _playerState.postValue(PlayerState.Paused)
            mainThreadHandler.removeCallbacksAndMessages(null)
        }
    }

    private fun resetTimer() {
        time = START_TIME
        postTime(0)
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun countTime() {
        mainThreadHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    postTime(time)
                    time++
                    mainThreadHandler.postDelayed(this, 1000L)
                }
            }, 1000L
        )
    }

    private fun postTime(time: Int) {
        val min = time / 60
        val sec = time % 60
        val strTime = "%02d".format(min) + ":" + "%02d".format(sec)
        _playerTime.postValue(strTime)
    }
}