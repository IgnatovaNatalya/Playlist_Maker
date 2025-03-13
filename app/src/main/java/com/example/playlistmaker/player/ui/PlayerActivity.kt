package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.intractor.PlaybackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.activity.SearchActivity.Companion.EXTRA_TRACK

import com.google.android.material.appbar.MaterialToolbar

const val START_TIME = 1

class PlayerActivity : AppCompatActivity() {

    private lateinit var btnPlayPause: ImageView
    private lateinit var txTimer: TextView

    private var time = START_TIME
    private var mainThreadHandler: Handler? = null

    private lateinit var playbackInteractor: PlaybackInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playbackInteractor = Creator.providePlaybackInteractor()

        val toolbar = findViewById<MaterialToolbar>(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val intent = intent

        @Suppress("DEPRECATION")
        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)

        btnPlayPause = findViewById(R.id.button_play_pause)

        playbackInteractor.preparePlayer(
            track?.previewUrl,
            { btnPlayPause.isEnabled = true },
            {
                btnPlayPause.setImageResource(R.drawable.button_play)
                time = START_TIME
                setTime(0)
                mainThreadHandler?.removeCallbacksAndMessages(null)
            })


        btnPlayPause.setOnClickListener {
            playbackInteractor.playbackControl()

            if (playbackInteractor.isPlaying()) {
                btnPlayPause.setImageResource(R.drawable.button_pause)
                countTime()
            }
            else {
                btnPlayPause.setImageResource(R.drawable.button_play)
                mainThreadHandler?.removeCallbacksAndMessages(null)
            }
        }

        val albumPicture = findViewById<ImageView>(R.id.player_album_picture)
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, albumPicture.resources.displayMetrics
        ).toInt()

        val pictureUrl = track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        Glide.with(albumPicture)
            .load(pictureUrl)
            .centerInside()
            .transform(RoundedCorners(radiusPx))
            .placeholder(R.drawable.album_placeholder)
            .into(albumPicture)

        val tvTitle = findViewById<TextView>(R.id.player_title)
        tvTitle.text = track?.trackName

        val tvBand = findViewById<TextView>(R.id.player_band)
        tvBand.text = track?.artistName

        val tvDuration = findViewById<TextView>(R.id.player_duration)
        tvDuration.text = track?.duration

        val tvAlbum = findViewById<TextView>(R.id.player_album)
        tvAlbum.text = track?.collectionName

        val tvYear = findViewById<TextView>(R.id.player_year)
        if (track != null) tvYear.text =
            with(track) { if (releaseDate.length > 4) releaseDate.substring(0, 4) else releaseDate }

        val tvGenre = findViewById<TextView>(R.id.player_genre)
        tvGenre.text = track?.primaryGenreName

        val tvCountry = findViewById<TextView>(R.id.player_country)
        tvCountry.text = track?.country

        txTimer = findViewById(R.id.playback_timer)
        mainThreadHandler = Handler(Looper.getMainLooper())
    }

    override fun onPause() {
        super.onPause()
        playbackInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackInteractor.releasePlayer()
    }

    private fun countTime() {
        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    setTime(time)
                    time++
                    mainThreadHandler?.postDelayed(this, 1000L)
                }
            }, 1000L
        )
    }

    private fun setTime(time: Int) {
        val min = time / 60
        val sec = time % 60
        val strTime = "%02d".format(min) + ":" + "%02d".format(sec)
        txTimer.text = strTime
    }
}