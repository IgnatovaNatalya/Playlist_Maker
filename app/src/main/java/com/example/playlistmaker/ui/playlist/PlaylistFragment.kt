package com.example.playlistmaker.ui.playlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import org.koin.core.parameter.parametersOf

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(requireArguments().getInt(EXTRA_PLAYLIST_ID))
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
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

//        requireArguments().getInt(EXTRA_PLAYLIST_ID).let { playlistId ->
//            viewModel.getPlaylist(playlistId)
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
            if (state != null) renderState(state)
        }

        viewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            trackAdapter?.tracks = tracks
            trackAdapter?.notifyDataSetChanged()
        }

        viewModel.toastState.observe(viewLifecycleOwner) { toastText ->
            if (!toastText.isNullOrBlank()) showToast(toastText)
        }

        binding.playlistToolbar.setNavigationOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.popBackStack()
        }

        binding.btnSharePlaylist.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.btnMenu.setOnClickListener {
            //еще один Боттом
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED ->
                        binding.overlay.visibility = View.GONE

                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun showToast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }


    private fun renderState(state: PlaylistUiState) {
        when (state) {
            is PlaylistUiState.Loading -> showLoading()
            is PlaylistUiState.Content -> showContent(state.playlist)
        }

    }

    fun showLoading() {
        binding.clPlaylistContent.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    fun showContent(playlist: Playlist) {
        binding.clPlaylistContent.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        drawPlaylist(playlist)
    }

    private fun drawPlaylist(playlist: Playlist) {


        Glide.with(binding.playlistCover)
            .load(playlist.path)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(binding.playlistCover)

        binding.playlistTitle.text = playlist.title
        binding.playlistDescription.text = playlist.description
        val str = binding.root.resources.getQuantityString(
            R.plurals.tracks,
            playlist.numTracks,
            playlist.numTracks
        )
        binding.playlistNumTracks.text = str
        binding.playlistDuration.text = "Посчитать"
    }

    override fun onResume() {
        super.onResume()
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "EXTRA_PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle = bundleOf(EXTRA_PLAYLIST_ID to playlistId)
    }
}