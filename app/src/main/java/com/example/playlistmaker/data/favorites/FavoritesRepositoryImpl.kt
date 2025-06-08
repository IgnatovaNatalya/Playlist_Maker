package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.toListTrack
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        appDatabase.favoritesDao().addToFavorites(track.toFavTrackEntity())
    }

    override suspend fun removeFromFavorites(track: Track) {
        appDatabase.favoritesDao().removeFromFavorites(track.toFavTrackEntity())
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        val favTracks = appDatabase.favoritesDao().getFavoriteTracks()
        return favTracks.map { toListTrack(it) }
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> = flow {
        emit(appDatabase.favoritesDao().getFavoriteTrackIds())
    }
}