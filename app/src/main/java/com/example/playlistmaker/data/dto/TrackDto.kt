package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Track

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
    trackTimeMillis,
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl
)


