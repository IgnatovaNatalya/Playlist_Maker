package com.example.playlistmaker.util

import com.example.playlistmaker.R

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonResource: Int,
    val progress: String,
) {
    class Default(): PlayerState(false, R.drawable.button_play, "00:00")

    class Prepared() : PlayerState(true, R.drawable.button_play, "00:00")

    class Playing(progress: String) :
        PlayerState(true, R.drawable.button_pause, progress)

    class Paused(progress: String) :
        PlayerState( true, R.drawable.button_play, progress)
}

class PlayerUiState(
    val isPlayButtonEnabled: Boolean,
    val buttonResource: Int,
    val progress: String,
    val isFavorite:Boolean
)

