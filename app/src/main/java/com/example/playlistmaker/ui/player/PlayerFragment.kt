package com.example.playlistmaker.ui.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.PlayerUiState
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.viewmodel.PlaybackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val viewModel: PlaybackViewModel by viewModel()
    private var playlistsLinearAdapter: PlaylistLinearAdapter? = null
    private lateinit var playlistsLinearRecycler: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private lateinit var onPlaylistCreateDebounce: (Unit) -> Unit
    private lateinit var currentTrack: Track

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().getParcelable<Track>(EXTRA_TRACK)?.let { track ->
            viewModel.setTrack(track)
            viewModel.preparePlayer(track)
            currentTrack = track
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.addTrackTo(playlist)
        }

        onPlaylistCreateDebounce = debounce<Unit>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            requireActivity().findNavController(R.id.fragment_container)
                .navigate(R.id.newPlaylistFragment)
        }

        drawTrack(currentTrack)

        playlistsLinearAdapter =
            PlaylistLinearAdapter { playlist -> onPlaylistClickDebounce(playlist) }

        playlistsLinearRecycler = binding.playlistsLinearRecycler
        playlistsLinearRecycler.layoutManager = LinearLayoutManager(requireContext())
        playlistsLinearRecycler.adapter = playlistsLinearAdapter

        viewModel.playerUiState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.toastState.observe(viewLifecycleOwner) { toastText ->
            if (!toastText.isNullOrBlank()) showToast(toastText)
        }

        viewModel.playlists.observe(viewLifecycleOwner) {
            playlistsLinearAdapter?.playlists = it
            playlistsLinearAdapter?.notifyDataSetChanged()
        }

        binding.playerToolbar.setNavigationOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.popBackStack()
        }

        binding.buttonPlayPause.setOnClickListener { viewModel.onPlayButtonClicked() }
        binding.buttonLike.setOnClickListener { viewModel.onLikeClicked() }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.buttonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnCreatePlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            onPlaylistCreateDebounce(Unit)
        }
    }

    private fun showToast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }

    private fun renderState(state: PlayerUiState) {
        binding.buttonPlayPause.isEnabled = state.isPlayButtonEnabled
        binding.buttonPlayPause.setImageResource(state.buttonResource)
        binding.playbackTimer.text = state.progress
        renderFav(state.isFavorite)
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

        renderFav(track.isFavorite)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    fun renderFav(isFav: Boolean) {
        with(binding.buttonLike) {
            if (isFav) setImageResource(R.drawable.button_liked)
            else setImageResource(R.drawable.button_like)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playlistsLinearAdapter = null
        playlistsLinearRecycler.adapter = null
    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
        const val CLICK_DEBOUNCE_DELAY = 300L

        fun createArgs(track: Track): Bundle = bundleOf(EXTRA_TRACK to track)
    }
}