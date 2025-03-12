package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.intaractor.HistoryInteractor
import com.example.playlistmaker.search.domain.intaractor.SearchTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.SearchResult
import com.example.playlistmaker.player.ui.PlayerActivity


const val EXTRA_TRACK = "EXTRA_TRACK_STR"
const val CLICK_DEBOUNCE_DELAY = 1000L
const val SEARCH_DEBOUNCE_DELAY = 2000L

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var searchAdapter = TrackAdapter { openPlayer(it) }
    private var historyAdapter = TrackAdapter { openPlayer(it) }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchTracks() }

    private lateinit var tracksInteractor: SearchTracksInteractor
    private lateinit var historyInteractor: HistoryInteractor

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        historyInteractor = Creator.provideHistoryInteractor(this)
        tracksInteractor = Creator.provideTracksInteractor()

        historyInteractor.getSavedHistory()

        binding.searchToolbar.setNavigationOnClickListener { finish() }

        binding.clearSearchButton.setOnClickListener {
            binding.searchInputText.setText(DEFAULT_TEXT)
            binding.searchRecycler.visibility = View.GONE
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(binding.clearSearchButton.windowToken, 0)
            setHistoryVisibility(true)
        }

        binding.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enteredText = s.toString()
                binding.clearSearchButton.visibility = clearButtonVisibility(s)
                binding.searchRecycler.visibility = View.GONE
                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
                setHistoryVisibility(binding.searchInputText.hasFocus() && s?.isEmpty() == true)
                searchDebounce()
            }
        })

        binding.searchInputText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && historyInteractor.getTracks().isNotEmpty()) {
                setHistoryVisibility(hasFocus)
            }
        }

        binding.searchRecycler.layoutManager = LinearLayoutManager(this)

        binding.searchRecycler.adapter = searchAdapter

        binding.searchReloadButton.setOnClickListener {
            setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
            searchDebounce()
        }

        binding.clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            historyAdapter.notifyDataSetChanged()
            setHistoryVisibility(false)
            binding.searchRecycler.visibility = View.GONE
        }
    }

    private fun searchTracks() {
        if (enteredText != "") {

            binding.placeholderMessage.visibility = View.GONE
            binding.searchReloadButton.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            tracksInteractor.searchTracks(
                enteredText,
                object : SearchTracksInteractor.TracksConsumer {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun consume(searchResult: SearchResult) {
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            when (searchResult.resultCode) {
                                200 -> {
                                    if (searchResult.results.isNotEmpty()) {
                                        searchAdapter.tracks = searchResult.results
                                        binding.searchRecycler.visibility = View.VISIBLE
                                        searchAdapter.notifyDataSetChanged()
                                    } else {
                                        setPlaceHolder(PlaceholderMessage.MESSAGE_NOT_FOUND)
                                    }
                                }

                                else -> {
                                    setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
                                }
                            }
                        }
                    }
                })
        }
    }

    fun setHistoryVisibility(searchFieldEmpty: Boolean) {
        if (searchFieldEmpty) {
            binding.historyHeader.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
            historyAdapter.tracks = historyInteractor.getTracks()
            binding.searchRecycler.adapter = historyAdapter
            binding.searchRecycler.visibility = View.VISIBLE
        } else {
            binding.historyHeader.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
            binding.searchRecycler.adapter = searchAdapter
        }
    }

    override fun onPause() {
        historyInteractor.saveHistory()
        super.onPause()
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            historyInteractor.addTrackToHistory(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(EXTRA_TRACK, track)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setPlaceHolder(message: PlaceholderMessage) {
        when (message) {
            PlaceholderMessage.MESSAGE_CLEAR -> {
                binding.placeholderMessage.visibility = View.GONE
                binding.searchReloadButton.visibility = View.GONE
            }

            PlaceholderMessage.MESSAGE_NO_INTERNET,
            PlaceholderMessage.MESSAGE_NOT_FOUND -> {
                binding.placeholderMessage.visibility = View.VISIBLE

                if (message == PlaceholderMessage.MESSAGE_NO_INTERNET)
                    binding.searchReloadButton.visibility = View.VISIBLE
                else binding.searchReloadButton.visibility = View.GONE

                binding.searchRecycler.visibility = View.GONE
                binding.placeholderMessage.text = this.getString(message.resText)
                binding.placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(0, message.image, 0, 0)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        enteredText = savedInstanceState.getString(ENTERED_TEXT, DEFAULT_TEXT)

        if (enteredText.isNotEmpty()) {
            binding.searchInputText.setText(enteredText)
            searchTracks()
        }
    }

    private fun searchDebounce() { //дебаунс в поисковой строке
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean { //дебаунс для нажатия на обложку
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private var enteredText: String = DEFAULT_TEXT

    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val DEFAULT_TEXT = ""
    }
}