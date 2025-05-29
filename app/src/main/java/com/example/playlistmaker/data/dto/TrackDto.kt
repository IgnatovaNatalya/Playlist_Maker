package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country:String,
    val previewUrl: String
)

fun TrackDto.toTrack() = Track(
    trackId,
    trackName,
    artistName,
    SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis),
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl
)
