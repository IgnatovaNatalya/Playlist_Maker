package com.example.playlistmaker.ui.playlists

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.RootActivity
import com.example.playlistmaker.ui.playlist.PlaylistFragment
import com.example.playlistmaker.ui.search.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.GridSpacingItemDecoration
import com.example.playlistmaker.util.PlaylistsState
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private var playlistsAdapter: PlaylistGridAdapter? = null
    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private lateinit var playlistsRecycler: RecyclerView

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?)
            : FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            (activity as RootActivity).animateBottomNavigationView()
            requireActivity().findNavController(R.id.fragment_container).navigate(
                R.id.action_mediaFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlist.id)
            )
            //findNavController().navigate(R.id.action_playlistsFragment_to_playlistFragment,
        }

        playlistsAdapter = PlaylistGridAdapter { playlist -> onPlaylistClickDebounce(playlist) }

        viewModel.playlistsState.observe(viewLifecycleOwner) { render(it) }

        playlistsRecycler = binding.playlistsRecycler
        playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        playlistsRecycler.adapter = playlistsAdapter

        val spacingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, requireContext().resources.displayMetrics
        ).toInt()

        playlistsRecycler.addItemDecoration(GridSpacingItemDecoration(2, spacingPx, false))

        binding.btnCreatePlaylist.setOnClickListener {
            requireActivity().findNavController(R.id.fragment_container)
                .navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Loading -> showLoading()
            is PlaylistsState.Content -> showContent(state)
        }
    }

    private fun showEmpty() {
        binding.playlistsRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.placeholderMessage.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.playlistsRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(state: PlaylistsState.Content) {
        playlistsAdapter?.playlists = state.playlists
        playlistsAdapter?.notifyDataSetChanged()

        binding.playlistsRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}