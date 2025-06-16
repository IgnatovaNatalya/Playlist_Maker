package com.example.playlistmaker.util

import com.example.playlistmaker.domain.model.Track

data class PlayerUiState(
    val track:Track,
    val playerState: PlayerState,
    val isFavorite:Boolean
)
