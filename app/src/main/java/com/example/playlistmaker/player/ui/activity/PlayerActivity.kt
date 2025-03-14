package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.PlaybackViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.activity.SearchActivity.Companion.EXTRA_TRACK

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlaybackViewModel

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

        viewModel = ViewModelProvider(
            this,
            PlaybackViewModel.getViewModelFactory()
        )[PlaybackViewModel::class.java]

        binding.playerToolbar.setNavigationOnClickListener { finish() }

        viewModel.playerState.observe(this) { state -> renderState(state) }
        viewModel.playerTime.observe(this) { strTime -> renderTime(strTime) }

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

            PlayerState.Prepared -> binding.buttonPlayPause.isEnabled = true
            PlayerState.Playing -> showPlaying()
            PlayerState.Paused -> showPaused()
            PlayerState.Completed -> showPaused()
        }
    }

    private fun renderTime(strTime: String) {
        binding.playbackTimer.text = strTime
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

    private fun showPlaying() {
        binding.buttonPlayPause.setImageResource(R.drawable.button_pause)
    }

    private fun showPaused() {
        binding.buttonPlayPause.setImageResource(R.drawable.button_play)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }
}