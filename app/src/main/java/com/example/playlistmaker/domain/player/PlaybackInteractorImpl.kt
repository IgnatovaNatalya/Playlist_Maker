package com.example.playlistmaker.domain.player

import com.example.playlistmaker.data.player.PlaybackRepository

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