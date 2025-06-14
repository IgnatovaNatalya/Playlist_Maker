package com.example.playlistmaker.util

import com.example.playlistmaker.R

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonResource: Int,
    val progress: String,
    var isFavorite: Boolean = false,
) {
    class Default( isFavorite: Boolean) :
        PlayerState( false, R.drawable.button_play, "00:00", isFavorite)

    class Prepared( isFavorite: Boolean) :
        PlayerState( true, R.drawable.button_play, "00:00", isFavorite)

    class Playing( progress: String, isFavorite: Boolean) :
        PlayerState( true, R.drawable.button_pause, progress, isFavorite)

    class Paused(progress: String, isFavorite: Boolean) :
        PlayerState( true, R.drawable.button_play, progress, isFavorite)
}


