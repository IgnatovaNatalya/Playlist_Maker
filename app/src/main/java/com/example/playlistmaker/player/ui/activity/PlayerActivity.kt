package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.PlaybackViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.activity.SearchActivity.Companion.EXTRA_TRACK
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlaybackViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.playerToolbar.setNavigationOnClickListener { finish() }

        viewModel.playerState.observe(this) { state -> renderState(state) }

        val intent = intent

        @Suppress("DEPRECATION")
        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)

        if (track != null) {
            drawTrack(track)
            viewModel.preparePlayer(track)
        }

        binding.buttonPlayPause.setOnClickListener { viewModel.playbackControl() }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.NotPrepared -> {
                showPaused()
                binding.buttonPlayPause.isEnabled = false
            }

            PlayerState.Prepared -> {
                binding.buttonPlayPause.isEnabled = true

            }
            is PlayerState.Playing -> showPlaying(state.playerTime)
            is PlayerState.Paused -> showPaused()
            PlayerState.Completed -> {
                showPaused()
                binding.playbackTimer.text = formatTime(0)
            }
        }
    }

    private fun drawTrack(track: Track) {

        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, binding.playerAlbumPicture.resources.displayMetrics
        ).toInt()

        val pictureUrl = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        Glide.with(binding.playerAlbumPicture)
            .load(pictureUrl)
            .centerInside()
            .transform(RoundedCorners(radiusPx))
            .placeholder(R.drawable.album_placeholder)
            .into(binding.playerAlbumPicture)

        binding.playerTitle.text = track.trackName
        binding.playerBand.text = track.artistName
        binding.playerDuration.text = track.duration
        binding.playerAlbum.text = track.collectionName
        binding.playerYear.text = if (track.releaseDate.length > 4) track.releaseDate.substring(
            0,
            4
        ) else track.releaseDate
        binding.playerGenre.text = track.primaryGenreName
        binding.playerCountry.text = track.country
    }

    private fun showPlaying(time: Int) {
        binding.buttonPlayPause.setImageResource(R.drawable.button_pause)
        binding.playbackTimer.text = formatTime(time)
    }

    private fun showPaused() {
        binding.buttonPlayPause.setImageResource(R.drawable.button_play)
    }

    override fun onPause() {
        super.onPause()
        //viewModel.pausePlayer()
        viewModel.releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun formatTime(time:Int) :String{
        val min = time / 60
        val sec = time % 60
        return "%02d".format(min) + ":" + "%02d".format(sec)
    }
}