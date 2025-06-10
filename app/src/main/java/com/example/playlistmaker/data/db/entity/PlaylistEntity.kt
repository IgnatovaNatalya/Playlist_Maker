package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Playlist

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description:String,
    val path:String,
    val numTracks: Int
) {
    fun toPlaylist(): Playlist {
        return Playlist(
            id, title, description, path, numTracks
        )
    }
}

fun toListPlaylist(playEntityLists: List<PlaylistEntity>) =
    playEntityLists.map { it.toPlaylist() }
