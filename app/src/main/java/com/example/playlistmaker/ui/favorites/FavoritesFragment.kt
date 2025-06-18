package com.example.playlistmaker.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.RootActivity
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.ui.search.SearchFragment
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.FavoritesState
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BindingFragment<FragmentFavoritesBinding>() {

    private var tracksAdapter: TrackAdapter? = null
    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var tracksRecycler: RecyclerView

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            SearchFragment.Companion.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            requireActivity().findNavController(R.id.fragment_container).navigate(
                R.id.playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        tracksAdapter = TrackAdapter(
            clickListener = { track ->
                (activity as RootActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            }
        )


        viewModel.favoritesState.observe(viewLifecycleOwner) { render(it) }

        tracksRecycler = binding.favTracksRecycler
        tracksRecycler.layoutManager = LinearLayoutManager(activity)
        tracksRecycler.adapter = tracksAdapter
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showEmpty()
            is FavoritesState.Loading -> showLoading()
            is FavoritesState.Content -> showContent(state)
        }
    }

    private fun showEmpty() {
        binding.favTracksRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.placeholderMessage.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.favTracksRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(state: FavoritesState.Content) {
        tracksAdapter?.tracks = state.favoriteTracks
        tracksAdapter?.notifyDataSetChanged()

        binding.favTracksRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksAdapter = null
        tracksRecycler.adapter = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}