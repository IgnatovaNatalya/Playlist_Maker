package com.example.playlistmaker.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.util.BindingFragment

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?)
            : FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    //private val viewModel: PlaylistsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreatePlaylist.setOnClickListener {
            val rootNavController = requireActivity().findNavController(R.id.fragment_container)
            rootNavController.navigate(R.id.newPlaylistFragment)
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}