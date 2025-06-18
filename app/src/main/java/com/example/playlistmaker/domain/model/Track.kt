package com.example.playlistmaker.domain.model

import android.os.Parcelable
import com.example.playlistmaker.data.db.entity.FavTrackEntity
import com.example.playlistmaker.data.db.entity.HistoryEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.parcelize.Parcelize
import java.lang.System.currentTimeMillis

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable {

    fun toTrackEntity(): TrackEntity {
        return TrackEntity(
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    fun toHistoryEntity(track: Track): HistoryEntity {
        return with(track) {
            HistoryEntity(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
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

    fun toFavTrackEntity(): FavTrackEntity {
        return FavTrackEntity(
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl,
            currentTimeMillis()
        )
    }
}
