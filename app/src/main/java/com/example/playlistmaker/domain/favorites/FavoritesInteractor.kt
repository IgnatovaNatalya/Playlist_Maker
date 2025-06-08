package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track:Track)
    fun getFavoriteTracks():Flow<List<Track>>
    fun getFavoriteTrackIds(): Flow<List<Int>>
}