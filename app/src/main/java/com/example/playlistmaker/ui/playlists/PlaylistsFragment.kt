package com.example.playlistmaker.ui.playlists

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.ui.RootActivity
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.PlaylistsState
import com.example.playlistmaker.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private var playlistsAdapter: PlaylistAdapter?=null
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistsRecycler: RecyclerView

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?)
            : FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsAdapter = PlaylistAdapter {playlist -> {}
            (activity as RootActivity).animateBottomNavigationView()
        }
        viewModel.playlistsState.observe(viewLifecycleOwner) { render(it)}

        playlistsRecycler = binding.playlistsRecycler
        playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsRecycler.adapter = playlistsAdapter

        binding.btnCreatePlaylist.setOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.navigate(R.id.newPlaylistFragment)
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