package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Playlist
import java.util.concurrent.TimeUnit

data class PlaylistDto(
    val id: Int,
    val title: String,
    val description:String,
    val path:String,
    val numTracks:Int,
    val totalDurationMillis:Long
) {
    fun toPlaylist(): Playlist {
        return Playlist(
            id, title, description, path, numTracks, TimeUnit.MILLISECONDS.toMinutes(totalDurationMillis).toInt()
        )
    }
}

fun toListPlaylist(listPlaylistsDto: List<PlaylistDto>) =
    listPlaylistsDto.map { it.toPlaylist() }
