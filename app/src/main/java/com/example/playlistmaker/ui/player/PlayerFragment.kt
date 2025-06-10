package com.example.playlistmaker.ui.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.PlayerState
import com.example.playlistmaker.viewmodel.PlaybackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment: BindingFragment<FragmentPlayerBinding>() {

    private val viewModel: PlaybackViewModel by viewModel()
    private var playlistsLinearAdapter: PlaylistLinearAdapter? = null
    private lateinit var playlistsLinearRecycler: RecyclerView

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater,container,false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playerToolbar.setNavigationOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.popBackStack()
        }

        binding.buttonLike.setOnClickListener { viewModel.onLikeClicked() }

        viewModel.playerState.observe(viewLifecycleOwner) { state -> renderState(state) }
        viewModel.favoriteState.observe(viewLifecycleOwner) { favoriteState -> renderFav(favoriteState) }
        viewModel.toastState.observe(viewLifecycleOwner) { toast -> showToast(toast) }

        val track:Track? = requireArguments().getParcelable<Track>(EXTRA_TRACK)

        if (track != null) {
            drawTrack(track)
            viewModel.preparePlayer(track)
        }

        binding.buttonPlayPause.setOnClickListener { viewModel.onPlayButtonClicked() }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistsLinearAdapter = PlaylistLinearAdapter({ playlist ->
            viewModel.addTrackTo(playlist)
        })

        viewModel.playlists.observe(viewLifecycleOwner) {
            playlistsLinearAdapter?.playlists = it
            playlistsLinearAdapter?.notifyDataSetChanged()
        }

        playlistsLinearRecycler = binding.playlistsLinearRecycler
        playlistsLinearRecycler.layoutManager = LinearLayoutManager(requireContext())
        playlistsLinearRecycler.adapter = playlistsLinearAdapter

        binding.buttonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

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

        binding.btnCreatePlaylist.setOnClickListener {
            requireActivity().findNavController(R.id.fragment_container).navigate(R.id.newPlaylistFragment)
        }

    }

    private fun showToast(toast:String) {
        Toast.makeText(requireContext(),toast, Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        playlistsLinearAdapter = null
        playlistsLinearRecycler.adapter = null
    }

    companion object {

        const val EXTRA_TRACK = "EXTRA_TRACK"

        fun createArgs(track: Track): Bundle =
            bundleOf(EXTRA_TRACK to track)
    }

}