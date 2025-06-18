package com.example.playlistmaker.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
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

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var trackAdapter: TrackAdapter? = null
    private lateinit var trackRecycler: RecyclerView

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().getInt(EXTRA_PLAYLIST_ID).let { playlistId ->
            viewModel.getPlaylist(playlistId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
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
        val onLongClick = { t:Track ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.remove_track)
                //.setMessage(R.string.info_erase_input)
                .setNeutralButton(R.string.no) { dialog, which -> }
                .setPositiveButton(R.string.yes) { dialog, which -> viewModel.removeTrack(t) }
                .show()
            true
        }

        trackAdapter = TrackAdapter (onTrackClickDebounce, onLongClick) //{ track -> onTrackClickDebounce(track) }

        trackRecycler = binding.playlistTrackRecycler
        trackRecycler.layoutManager = LinearLayoutManager(activity)
        trackRecycler.adapter = trackAdapter

        viewModel.playlistUiState.observe(viewLifecycleOwner) { state ->
            if (state != null) renderState(state)
        }

        viewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            trackAdapter?.tracks = tracks
        }

        binding.playlistToolbar.setNavigationOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.popBackStack()
        }



        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
//                    else -> binding.overlay.visibility = View.VISIBLE
//                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun renderState(state: PlaylistUiState) {
        when (state) {
            is PlaylistUiState.Loading -> showLoading()
            is PlaylistUiState.Content -> showContent(state.playlist)
        }

    }

    fun showLoading() {
//        binding.playlistCover.visibility = View.GONE
//        binding.playlistTitle.visibility = View.GONE
//        binding.llDurationNumTracks.visibility = View.GONE
//        binding.llButtons.visibility = View.GONE
        binding.clPlaylistContent.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    fun showContent(playlist: Playlist) {
//        binding.playlistCover.visibility = View.VISIBLE
//        binding.playlistTitle.visibility = View.VISIBLE
//        binding.llDurationNumTracks.visibility = View.VISIBLE
//        binding.llButtons.visibility = View.VISIBLE
        binding.clPlaylistContent.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        drawPlaylist(playlist)
    }

    private fun drawPlaylist(playlist: Playlist) {

//        val radiusPx = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, 8F, binding.playlistCover.resources.displayMetrics
//        )

//        Glide.with(binding.playlistCover)
//            .load(playlist.path)
//            .centerInside()
//            .transform(RoundedCorners(radiusPx))
//            .placeholder(R.drawable.album_placeholder)
//            .into(binding.playlistCover)

        Glide.with(binding.playlistCover)
            //.asBitmap()
            .load(playlist.path)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(binding.playlistCover)



        binding.playlistTitle.text = playlist.title
        binding.playlistDescritrion.text = playlist.description
        val str = binding.root.resources.getQuantityString(R.plurals.tracks, playlist.numTracks,playlist.numTracks)
        binding.playlistNumTracks.text = str
        binding.playlistDuration.text = "Посчитать"
    }

    override fun onResume() {
        super.onResume()
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "EXTRA_PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle = bundleOf(EXTRA_PLAYLIST_ID to playlistId)
    }
}