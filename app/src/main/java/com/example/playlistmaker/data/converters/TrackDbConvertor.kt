package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.model.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

    fun map(track: TrackDto): TrackEntity {
        return TrackEntity(track.id, track.resultType, movie.image, track.title, track.description)
    }

    fun map(track: TrackEntity): Track {
        return Track(track.id, track.image, track.title, track.description, false)
    }
}