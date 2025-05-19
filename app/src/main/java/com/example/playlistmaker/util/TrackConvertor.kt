package com.example.playlistmaker.util

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.model.Track
import java.lang.System.currentTimeMillis

class TrackConvertor {

    fun map(track: Track): TrackEntity {
        return with(track) {
            TrackEntity(
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
    }

//    fun map(track: TrackDto): TrackEntity {
//        return TrackEntity(track.id, track.resultType, movie.image, track.title, track.description)
//    }

    fun map(trackEntity: TrackEntity): Track {
        return with(trackEntity) {
            Track(
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
                true
            )
        }
    }
}