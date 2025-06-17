package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Track

@Entity(tableName = "track_table")
data class TrackEntity (
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val duration: String,
    val artworkUrl100: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country:String,
    val previewUrl: String
)

fun TrackEntity.toTrack() = Track(
    trackId, trackName, artistName, duration, artworkUrl100,
    collectionName, releaseDate, primaryGenreName, country, previewUrl
)

fun toListTrack(tracksList: List<TrackEntity>) =
    tracksList.map {
        it.toTrack()
    }
