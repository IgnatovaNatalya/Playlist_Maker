package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Track

@Entity(tableName = "history_track_table")
data class HistoryEntity(
    @PrimaryKey
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
    val added: Long,
    val isFavorite:Boolean
)

fun HistoryEntity.toTrack() = Track(
    trackId, trackName, artistName, duration, artworkUrl100,
    collectionName, releaseDate, primaryGenreName, country, previewUrl, isFavorite
)

fun toListTrack(tracksList: List<HistoryEntity>) =
    tracksList.map { it.toTrack() }

