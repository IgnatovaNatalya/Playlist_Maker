package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.dto.toTrack
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.model.SearchResult
import com.example.playlistmaker.domain.search.SearchTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
) : SearchTracksRepository {

    override fun searchTracks(expression: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            200 -> {
                val listTrack = (response as TracksSearchResponse).results.map { it.toTrack() }

                val favTracks = appDatabase.favoritesDao().getFavoriteTrackIds()

                for (track in listTrack)
                    if (favTracks.contains(track.trackId)) track.isFavorite = true

                emit(
                    SearchResult(
                        resultCode = 200,
                        results = listTrack.sortedByDescending { it.isFavorite })
                )
            }

            else -> {
                emit(SearchResult(resultCode = response.resultCode, results = listOf()))
            }
        }
    }
}