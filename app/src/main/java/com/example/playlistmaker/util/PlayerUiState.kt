package com.example.playlistmaker.util

data class PlayerUiState(
    val isPlayButtonEnabled: Boolean,
    val buttonResource: Int,
    val progress: String,
    val isFavorite:Boolean
)
