package com.example.playlistmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.core.BindingFragment
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.viewmodel.SearchState
import com.example.playlistmaker.search.ui.viewmodel.TracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private var tracksAdapter = TrackAdapter { openPlayer(it) }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var enteredText: String = DEFAULT_TEXT

    private val viewModel: TracksViewModel by viewModel()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSavedHistory()

        viewModel.searchState.observe(viewLifecycleOwner) { render(it) }

        binding.clearSearchButton.setOnClickListener {
            binding.searchInputText.setText(DEFAULT_TEXT)
            val manager = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(binding.clearSearchButton.windowToken, 0)
            viewModel.showHistory()
        }

        binding.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearSearchButton.visibility = clearButtonVisibility(s)
                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
                enteredText = s.toString()
                viewModel.onSearchTextChanged(enteredText)
            }
        })

        binding.searchInputText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.showHistory()
        }

        binding.tracksRecycler.layoutManager = LinearLayoutManager(activity)
        binding.tracksRecycler.adapter = tracksAdapter

        binding.placeholderReloadButton.setOnClickListener { viewModel.searchDebounce(enteredText) }
        binding.clearHistoryButton.setOnClickListener { viewModel.onClickHistoryClearButton() }

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
        tracksAdapter.tracks = state.historyTracks
        tracksAdapter.notifyDataSetChanged()

        binding.tracksRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
        setHistoryVisibility(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchContent(state: SearchState.SearchContent) {
        tracksAdapter.tracks = state.searchTracks
        tracksAdapter.notifyDataSetChanged()

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

    override fun onPause() {
        viewModel.saveHistory()
        super.onPause()
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            viewModel.addTrackToHistory(track)
            val intent = PlayerActivity.newInstance(requireContext(), track)
            startActivity(intent)
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

    private fun clickDebounce(): Boolean { //дебаунс для нажатия на обложку
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
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

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 2000L
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val DEFAULT_TEXT = ""
    }
}