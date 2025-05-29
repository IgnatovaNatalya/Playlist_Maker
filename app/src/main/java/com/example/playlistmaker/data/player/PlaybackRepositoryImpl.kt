package com.example.playlistmaker.data.player

import android.media.MediaPlayer

class PlaybackRepositoryImpl(private var mediaPlayer: MediaPlayer): PlaybackRepository {

    override fun preparePlayer(trackUrl: String?,
                               onPrepare: () -> Unit,
                               onComplete: () -> Unit ) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepare()
        }
        mediaPlayer.setOnCompletionListener {
            onComplete()
        }
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun isPlaying() : Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}