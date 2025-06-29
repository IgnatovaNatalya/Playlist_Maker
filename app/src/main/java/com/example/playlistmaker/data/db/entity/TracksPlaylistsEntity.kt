package com.example.playlistmaker.data.db.entity

import androidx.room.Entity

@Entity(tableName = "tracks_playlists_table", primaryKeys = ["trackId", "playlistId"])
data class TracksPlaylistsEntity(
    val trackId: Int,
    val playlistId: Int,
    val added: Long,
)
