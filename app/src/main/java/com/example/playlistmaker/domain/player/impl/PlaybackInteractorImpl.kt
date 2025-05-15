package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.data.player.PlaybackRepository
import com.example.playlistmaker.domain.player.PlaybackInteractor

class PlaybackInteractorImpl(private val repository: PlaybackRepository): PlaybackInteractor {

    override fun preparePlayer(
        trackUrl: String?,
        onPrepare: () -> Unit,
        onComplete: () -> Unit
    ) {
        repository.preparePlayer (trackUrl,onPrepare, onComplete)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return  repository.getCurrentPosition()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }
}