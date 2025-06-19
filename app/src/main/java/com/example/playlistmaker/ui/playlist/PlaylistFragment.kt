package com.example.playlistmaker.ui.playlist

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.newPlaylist.NewPlaylistFragment
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.ui.search.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.PlaylistUiState
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.viewmodel.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(requireArguments().getInt(EXTRA_PLAYLIST_ID))
    }

    private lateinit var bottomSheetTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetMenu: BottomSheetBehavior<LinearLayout>

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var trackAdapter: TrackAdapter? = null
    private lateinit var trackRecycler: RecyclerView

    private var playlistTitle = ""

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomSheets()

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
        val onLongClick = { t: Track ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.remove_track)
                .setNeutralButton(R.string.no) { dialog, which -> }
                .setPositiveButton(R.string.yes) { dialog, which -> viewModel.removeTrack(t) }
                .show()
            true
        }

        trackAdapter = TrackAdapter(onTrackClickDebounce, onLongClick)

        trackRecycler = binding.playlistTrackRecycler
        trackRecycler.layoutManager = LinearLayoutManager(activity)
        trackRecycler.adapter = trackAdapter

        viewModel.playlistUiState.observe(viewLifecycleOwner) { state ->
                renderState(state)
        }

        viewModel.toastState.observe(viewLifecycleOwner) { toastText ->
            if (!toastText.isNullOrBlank()) showToast(toastText)
        }

        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSharePlaylist.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.btnMenu.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnMenuShare.setOnClickListener {
            viewModel.sharePlaylist()
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.btnMenuDelete.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.confirm_delete_playlist, playlistTitle))
                .setNeutralButton(R.string.no) { dialog, which -> }
                .setPositiveButton(R.string.yes) { dialog, _ -> deleteAndClose() }
                .show()
        }

        binding.btnMenuEdit.setOnClickListener {
            requireActivity().findNavController(R.id.fragment_container)
                .navigate(
                    R.id.action_playlistFragment_to_newPlaylistFragment,
                    NewPlaylistFragment.createArgs(requireArguments().getInt(EXTRA_PLAYLIST_ID))
                )
        }
    }

    private fun deleteAndClose() {
        viewModel.deletePlaylist()
        findNavController().popBackStack()
    }

    private fun setBottomSheets() {
        bottomSheetTracks = BottomSheetBehavior.from(binding.bottomSheetTracks).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetMenu = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetTracks = BottomSheetBehavior.from(binding.bottomSheetTracks).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(OverlayBottomSheetCallback(
                hideOverlayStates = setOf(
                    BottomSheetBehavior.STATE_HIDDEN,
                    BottomSheetBehavior.STATE_COLLAPSED
                )
            ))
        }

        bottomSheetMenu = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(OverlayBottomSheetCallback(
                hideOverlayStates = setOf(BottomSheetBehavior.STATE_HIDDEN)
            ))
        }
    }

    private inner class OverlayBottomSheetCallback(
        private val hideOverlayStates: Set<Int>
    ) : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            binding.overlay.visibility = if (hideOverlayStates.contains(newState)) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun showToast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }

    private fun renderState(state: PlaylistUiState) {
        when (state) {
            is PlaylistUiState.Loading -> showLoading()
            is PlaylistUiState.Content -> showContent(state)
        }
    }

    fun showLoading() {
        binding.clPlaylistContent.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    fun showContent(state: PlaylistUiState.Content) {
        binding.clPlaylistContent.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        playlistTitle = state.playlist.title
        drawPlaylist(state.playlist)
        drawPlaylistBottom(state.playlist)

        if (state.tracks.isNotEmpty())
            bottomSheetTracks.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            bottomSheetTracks.state = BottomSheetBehavior.STATE_HIDDEN

        trackAdapter?.updateTracks(state.tracks)
    }

    private fun drawPlaylistBottom(playlist: Playlist) {

        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, COVER_CORNER_RADIUS_DP_SMALL, resources.displayMetrics
        )

        Glide.with(binding.itemPlaylistLinear.playlistLinearCover)
            .asBitmap()
            .load(playlist.path)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(object : BitmapImageViewTarget(binding.itemPlaylistLinear.playlistLinearCover) {
                override fun setResource(resource: Bitmap?) {
                    if (resource == null) return
                    val rounded =
                        RoundedBitmapDrawableFactory.create(resources, resource)
                            .apply { cornerRadius = radiusPx }
                    binding.itemPlaylistLinear.playlistLinearCover.setImageDrawable(rounded)
                }
            })

        binding.itemPlaylistLinear.playlistLinearTitle.text = playlist.title
        val strNumTracks = binding.root.resources.getQuantityString(
            R.plurals.tracks,
            playlist.numTracks,
            playlist.numTracks
        )
        binding.itemPlaylistLinear.playlistLinearNumTracks.text = strNumTracks

        if (playlist.numTracks ==0) binding.msgNoTracks.visibility=View.VISIBLE
        else binding.msgNoTracks.visibility=View.GONE

    }

    private fun drawPlaylist(playlist: Playlist) {

        Glide.with(binding.playlistCover)
            .load(playlist.path)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(binding.playlistCover)

        binding.playlistTitle.text = playlist.title
        binding.playlistDescription.text = playlist.description
        val strNumTracks = binding.root.resources.getQuantityString(
            R.plurals.tracks,
            playlist.numTracks,
            playlist.numTracks
        )
        binding.playlistNumTracks.text = strNumTracks
        val strDuration = binding.root.resources.getQuantityString(
            R.plurals.minutes,
            playlist.totalDurationMinutes,
            playlist.totalDurationMinutes
        )
        binding.playlistDuration.text = strDuration
    }


    override fun onResume() {
        super.onResume()
        viewModel.reloadData()
        bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "EXTRA_PLAYLIST_ID"
        const val COVER_CORNER_RADIUS_DP_SMALL = 2F
        fun createArgs(playlistId: Int): Bundle = bundleOf(EXTRA_PLAYLIST_ID to playlistId)
    }
}