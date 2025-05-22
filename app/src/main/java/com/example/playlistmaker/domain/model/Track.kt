package com.example.playlistmaker.domain.model

import android.os.Parcelable
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.HistoryEntity
import kotlinx.parcelize.Parcelize
import java.lang.System.currentTimeMillis

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val duration: String,
    val artworkUrl100: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable

fun Track.toHistoryEntity(track: Track): HistoryEntity {
    return with(track) {
        HistoryEntity(
            trackId,
            trackName,
            artistName,
            duration,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl,
            currentTimeMillis(),
            isFavorite
        )
    }
}

fun Track.toTrackEntity(): TrackEntity {
    return TrackEntity(
        trackId,
        trackName,
        artistName,
        duration,
        artworkUrl100,
        collectionName,
        releaseDate,
        primaryGenreName,
        country,
        previewUrl,
        currentTimeMillis()
    )
}
