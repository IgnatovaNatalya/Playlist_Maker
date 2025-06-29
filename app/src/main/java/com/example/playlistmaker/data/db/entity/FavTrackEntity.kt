package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Track

@Entity(tableName = "fav_track_table")
data class FavTrackEntity (
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country:String,
    val previewUrl: String,
    var added:Long
)

fun FavTrackEntity.toTrack() = Track(
    trackId, trackName, artistName, trackTimeMillis, artworkUrl100,
    collectionName, releaseDate, primaryGenreName, country, previewUrl, true
)

fun toListTrack(tracksList: List<FavTrackEntity>) =
    tracksList.map {
        it.toTrack()
    }