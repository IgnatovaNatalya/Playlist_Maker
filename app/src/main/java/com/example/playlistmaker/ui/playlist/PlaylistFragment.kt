package com.example.playlistmaker.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.viewmodel.PlaylistViewModel
import com.example.playlistmaker.util.PlaylistUiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: PlaylistViewModel by viewModel()

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

        viewModel.playlistUiState.observe(viewLifecycleOwner) { state ->
            if (state != null) renderState(state)
        }

        binding.playlistToolbar.setNavigationOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.popBackStack()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
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
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "EXTRA_PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle = bundleOf(EXTRA_PLAYLIST_ID to playlistId)
    }
}