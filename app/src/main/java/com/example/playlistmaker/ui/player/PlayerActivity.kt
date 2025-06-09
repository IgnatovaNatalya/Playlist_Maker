package com.example.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.PlayerState
import com.example.playlistmaker.viewmodel.PlaybackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlaybackViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*чтобы затемнение было на весь экран включая системные области*/

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        // Для Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        /*чтобы затемнение было на весь экран включая системные области*/

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        binding.playerToolbar.setNavigationOnClickListener { finish() }
        binding.buttonLike.setOnClickListener { viewModel.onLikeClicked() }

        viewModel.playerState.observe(this) { state -> renderState(state) }
        viewModel.favoriteState.observe(this) { favoriteState -> renderFav(favoriteState) }

        val intent = intent

        @Suppress("DEPRECATION")
        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)

        if (track != null) {
            drawTrack(track)
            viewModel.preparePlayer(track)
        }

        binding.buttonPlayPause.setOnClickListener { viewModel.onPlayButtonClicked() }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.buttonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED}

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun renderState(state: PlayerState) {
        binding.buttonPlayPause.isEnabled = state.isPlayButtonEnabled
        binding.buttonPlayPause.setImageResource(state.buttonResource)
        binding.playbackTimer.text = state.progress
    }

    private fun renderFav(favState: Boolean) {
        with(binding.buttonLike) {
            if (favState) setImageResource(R.drawable.button_liked)
            else setImageResource(R.drawable.button_like)
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