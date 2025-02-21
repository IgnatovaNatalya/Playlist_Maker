package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis), //трансформируем данные для передачи в domain-слой
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl)
            }
        } else {
            return emptyList()
        }

    }
}