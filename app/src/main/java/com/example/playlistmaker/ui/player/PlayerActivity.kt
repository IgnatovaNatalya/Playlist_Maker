package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
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
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.search.EXTRA_TRACK

import com.google.android.material.appbar.MaterialToolbar

const val START_TIME = 1

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private lateinit var play: ImageView
    private var mediaPlayer = MediaPlayer()
    private var time = START_TIME
    private var mainThreadHandler: Handler? = null
    private lateinit var txTimer: TextView

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

        @Suppress("DEPRECATION")
        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)

        play = findViewById(R.id.button_play_pause)
        preparePlayer(track?.previewUrl)
        play.setOnClickListener { playbackControl() }

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
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer(previewUrl: String?) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.button_play)
            playerState = STATE_PREPARED
            time = START_TIME
            setTime(0)
            mainThreadHandler?.removeCallbacksAndMessages(null)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
        countTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.button_play)
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
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