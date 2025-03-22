package com.example.playlistmaker.search.data.repositoryImpl

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.repository.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.SearchResult
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            200 -> {
                val listTrack = (response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
                return SearchResult(resultCode = 200, results = listTrack)
            }
            else -> {
                return SearchResult(resultCode = response.resultCode, results = listOf())
            }
        }
    }
}