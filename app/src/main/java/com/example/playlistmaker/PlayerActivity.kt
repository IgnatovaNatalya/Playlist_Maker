package com.example.playlistmaker

import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener { finish() }


        val intent = intent
        val trackStr = intent.getStringExtra(EXTRA_TRACK_STR)
        val gson = Gson()
        val track = gson.fromJson(trackStr, Track::class.java)

        //toolbar.title = track.trackName

        val albumPicture = findViewById<ImageView>(R.id.player_album_picture)
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4F, albumPicture.resources.displayMetrics
        ).toInt()

        val pictureUrl = track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

        Glide.with(albumPicture)
            .load(pictureUrl)
            .centerInside()
            .transform(RoundedCorners(radiusPx))
            .placeholder(R.drawable.album_placeholder)
            .into(albumPicture)

        val tvTitle = findViewById<TextView>(R.id.player_title)
        tvTitle.text = track.trackName

        val tvBand = findViewById<TextView>(R.id.player_band)
        tvBand.text = track.artistName

        val tvDuration = findViewById<TextView>(R.id.player_duration)
        tvDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val tvAlbum = findViewById<TextView>(R.id.player_album)
        tvAlbum.text = track.collectionName

        val tvYear = findViewById<TextView>(R.id.player_year)
        tvYear.text = track.releaseDate.substring(0,4)

        val tvGenre = findViewById<TextView>(R.id.player_genre)
        tvGenre.text = track.primaryGenreName

        val tvCountry = findViewById<TextView>(R.id.player_country)
        tvCountry.text = track.country
    }


}