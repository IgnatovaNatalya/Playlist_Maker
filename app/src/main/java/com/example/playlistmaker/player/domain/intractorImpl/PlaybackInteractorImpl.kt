package com.example.playlistmaker.player.domain.intractorImpl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.intractor.PlaybackInteractor

class PlaybackInteractorImpl: PlaybackInteractor {

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUrl: String?,
                               onPrepare: PlaybackInteractor.OnPreparedListener,
                               onComplete: PlaybackInteractor.OnCompletionListener ) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepare.onPrepare()
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            onComplete.onComplete()
            playerState = STATE_PREPARED
        }
    }

    override fun pausePlayer() {
        playerState = STATE_PAUSED
        mediaPlayer.pause()
    }

    override fun isPlaying() : Boolean {
        return (playerState == STATE_PLAYING)
    }

    override fun startPlayer() {
        playerState = STATE_PLAYING
        mediaPlayer.start()
    }

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }


    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}