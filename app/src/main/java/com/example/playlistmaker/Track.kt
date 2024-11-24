package com.example.playlistmaker

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)

val trackList = mutableListOf<Track>(
    Track(
        "Smells Like Teen Spirit","Nirvana","5:01",
        "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
    ),
    Track("Billie Jean", "Michael Jackson",  "4:35",
        "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg")
)