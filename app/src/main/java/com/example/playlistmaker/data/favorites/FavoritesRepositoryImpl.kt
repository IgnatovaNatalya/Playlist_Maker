package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.util.TrackConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackConvertor: TrackConvertor
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        val trackEntity = trackConvertor.map(track)
        appDatabase.favoritesDao().addToFavorites(trackEntity)
    }

    override suspend fun removeFromFavorites(track: Track) { // todo тут точно достаточно id
        val trackEntity = trackConvertor.map(track)
        appDatabase.favoritesDao().removeFromFavorites(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> = flow {
        val trackIds =  appDatabase.favoritesDao().getFavoriteTrackIds()
        emit(trackIds)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackConvertor.map(track) }
    }
}