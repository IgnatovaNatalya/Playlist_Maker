package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.ui.RootActivity
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.viewmodel.SearchTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private var enteredText: String = DEFAULT_TEXT
    private var tracksAdapter: TrackAdapter? = null
    private val viewModel: SearchTracksViewModel by viewModel()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var tracksRecycler: RecyclerView
    private lateinit var textWatcher: TextWatcher
    private lateinit var queryInput: EditText

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.addTrackToHistory(track)
            val intent = PlayerActivity.newInstance(requireContext(), track)
            startActivity(intent)
        }

        tracksAdapter = TrackAdapter { track ->
            (activity as RootActivity).animateBottomNavigationView()
            onTrackClickDebounce(track)
        }

        viewModel.searchState.observe(viewLifecycleOwner) { render(it) }

        binding.clearSearchButton.setOnClickListener {
            binding.searchInputText.setText(DEFAULT_TEXT)
            val manager = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(binding.clearSearchButton.windowToken, 0)
            viewModel.showHistory()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearSearchButton.visibility = clearButtonVisibility(s)
                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
                enteredText = s.toString()
                viewModel.onSearchTextChanged(enteredText)
            }
        }
        queryInput = binding.searchInputText
        queryInput.addTextChangedListener(textWatcher)

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            viewModel.showHistory()
        }

        tracksRecycler = binding.tracksRecycler
        tracksRecycler.layoutManager = LinearLayoutManager(activity)
        tracksRecycler.adapter = tracksAdapter

        binding.placeholderReloadButton.setOnClickListener { viewModel.searchDebounce(enteredText) }
        binding.clearHistoryButton.setOnClickListener { viewModel.onClickHistoryClearButton() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksAdapter = null
        tracksRecycler.adapter = null
        textWatcher.let { queryInput.removeTextChangedListener(it) }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Empty -> showEmpty()
            is SearchState.Error -> showError()
            is SearchState.Loading -> showLoading()
            is SearchState.HistoryContent -> showHistoryContent(state)
            is SearchState.SearchContent -> showSearchContent(state)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryContent(state: SearchState.HistoryContent) {
        tracksAdapter?.tracks = state.historyTracks
        tracksAdapter?.notifyDataSetChanged()

        binding.tracksRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchContent(state: SearchState.SearchContent) {
        tracksAdapter?.tracks = state.searchTracks
        tracksAdapter?.notifyDataSetChanged()

        binding.tracksRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(false)
    }

    private fun showEmpty() {
        binding.tracksRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        setPlaceHolder(PlaceholderMessage.MESSAGE_NOT_FOUND)
        setHistoryVisibility(false)
    }

    private fun showError() {
        binding.tracksRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
        setHistoryVisibility(false)
    }

    private fun showLoading() {
        binding.tracksRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(false)
    }

    fun setHistoryVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.historyHeader.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
        } else {
            binding.historyHeader.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
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


    companion object {
        const val CLICK_DEBOUNCE_DELAY = 300L
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val DEFAULT_TEXT = ""
    }
}