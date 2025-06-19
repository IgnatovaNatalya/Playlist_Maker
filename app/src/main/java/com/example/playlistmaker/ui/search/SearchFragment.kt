package com.example.playlistmaker.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.RootActivity
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.viewmodel.SearchTracksViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private var enteredText: String = DEFAULT_TEXT
    private var searchAdapter: TrackAdapter? = null
    private var historyAdapter: TrackAdapter? = null

    private val viewModel: SearchTracksViewModel by viewModel()

    private lateinit var onSearchTrackClickDebounce: (Track) -> Unit
    private lateinit var onHistoryTrackClickDebounce: (Track) -> Unit
    private lateinit var searchRecycler: RecyclerView
    private lateinit var historyRecycler: RecyclerView
    private lateinit var textWatcher: TextWatcher
    private lateinit var queryInput: EditText

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearchTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.addTrackToHistory(track)
            (activity as RootActivity).animateBottomNavigationView()
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        onHistoryTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            (activity as RootActivity).animateBottomNavigationView()
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        searchAdapter = TrackAdapter({ track -> onSearchTrackClickDebounce(track) })
        historyAdapter = TrackAdapter({ track -> onHistoryTrackClickDebounce(track) })

        observeViewModel()

        binding.clearSearchButton.setOnClickListener {
            binding.searchInputText.setText(DEFAULT_TEXT)
            val manager = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(binding.clearSearchButton.windowToken, 0)
            viewModel.onClearSearchClicked()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearSearchButton.visibility = clearSearchButtonVisibility(s)
                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
                enteredText = s.toString()
                viewModel.onSearchTextChanged(enteredText)
            }
        }
        queryInput = binding.searchInputText
        queryInput.addTextChangedListener(textWatcher)

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isEmpty())
                viewModel.onClearSearchClicked()
        }

        searchRecycler = binding.searchRecycler
        searchRecycler.layoutManager = LinearLayoutManager(activity)
        searchRecycler.adapter = searchAdapter

        historyRecycler = binding.historyRecycler
        historyRecycler.layoutManager = LinearLayoutManager(activity)
        historyRecycler.adapter = historyAdapter

        binding.placeholderReloadButton.setOnClickListener { viewModel.searchDebounce(enteredText) }
        binding.clearHistoryButton.setOnClickListener { viewModel.onClickHistoryClearButton() }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { searchState -> render(searchState) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchAdapter = null
        searchRecycler.adapter = null
        historyAdapter = null
        historyRecycler.adapter = null
        textWatcher.let { queryInput.removeTextChangedListener(it) }
    }


    fun render(state: SearchState) {
        when (state) {
            is SearchState.NotFound -> showNotFound()
            is SearchState.Error -> showSearchError()
            is SearchState.Loading -> showSearchLoading()
            is SearchState.SearchContent -> showSearchContent(state.foundTracks)
            is SearchState.HistoryContent -> showHistoryContent(state.historyTracks)
            SearchState.Empty -> showEmpty()
        }
    }


    private fun showHistoryContent(historyTracks: List<Track>) {
        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(true)

        historyAdapter?.updateTracks(historyTracks)
        binding.searchRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showSearchContent(foundTracks: List<Track>) {
        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(false)

        searchAdapter?.updateTracks(foundTracks)
        binding.searchRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showNotFound() {
        setPlaceHolder(PlaceholderMessage.MESSAGE_NOT_FOUND)
        setHistoryVisibility(false)

        binding.searchRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showEmpty() {
        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(false)

        binding.searchRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showSearchError() {
        setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
        setHistoryVisibility(false)

        binding.searchRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showSearchLoading() {
        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(false)

        binding.searchRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    fun setHistoryVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.historyHeader.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.historyRecycler.visibility = View.VISIBLE
        } else {
            binding.historyHeader.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
            binding.historyRecycler.visibility = View.GONE
        }
    }

    private fun setPlaceHolder(message: PlaceholderMessage) {
        when (message) {
            PlaceholderMessage.MESSAGE_CLEAR -> {
                binding.placeholderMessage.visibility = View.GONE
                binding.placeholderReloadButton.visibility = View.GONE
            }

            PlaceholderMessage.MESSAGE_NO_INTERNET -> {
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.placeholderReloadButton.visibility = View.VISIBLE
                binding.placeholderMessage.text = this.getString(message.text)
                binding.placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    message.image,
                    0,
                    0
                )
            }

            PlaceholderMessage.MESSAGE_NOT_FOUND -> {
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.placeholderReloadButton.visibility = View.GONE
                binding.placeholderMessage.text = this.getString(message.text)
                binding.placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    message.image,
                    0,
                    0
                )
            }
        }
    }

    private fun clearSearchButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty())
            View.GONE
        else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTERED_TEXT, enteredText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null)
            enteredText = savedInstanceState.getString(
                ENTERED_TEXT,
                DEFAULT_TEXT
            )
        if (enteredText.isNotEmpty()) {
            binding.searchInputText.setText(enteredText)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshContent()
    }

    override fun onPause() {
        super.onPause()
        textWatcher.let { queryInput.removeTextChangedListener(it) }
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 300L
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val DEFAULT_TEXT = ""
    }
}