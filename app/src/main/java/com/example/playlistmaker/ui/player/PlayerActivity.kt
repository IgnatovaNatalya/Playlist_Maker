package com.example.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
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
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.PlayerState
import com.example.playlistmaker.viewmodel.PlaybackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        binding.buttonPlayPause.setOnClickListener { viewModel.onPlayButtonClicked() }
    }

    private fun renderState(state: PlayerState) {
        binding.buttonPlayPause.isEnabled = state.isPlayButtonEnabled
        binding.buttonPlayPause.setImageResource(state.buttonResource)
        binding.playbackTimer.text = state.progress
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

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {

        const val EXTRA_TRACK = "EXTRA_TRACK"

        fun newInstance(context: Context, track: Track): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }
        }
    }
}