package com.example.playlistmaker.domain.model

import com.example.playlistmaker.data.db.entity.PlaylistEntity

data class Playlist(
    val id: Int,
    val title: String,
    val description: String,
    val path: String,
    val numTracks:Int,
    val totalDurationMinutes: Int
) {
    fun toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            id, title, description, path
        )
    }
}

