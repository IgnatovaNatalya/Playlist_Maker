package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.playlistmaker.PLAYLIST_MAKER_PREFS
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.player.PlayerActivity

import com.google.android.material.appbar.MaterialToolbar

const val EXTRA_TRACK = "EXTRA_TRACK_STR"
const val CLICK_DEBOUNCE_DELAY = 1000L
const val SEARCH_DEBOUNCE_DELAY = 2000L

class SearchActivity : AppCompatActivity() {

    private lateinit var clearSearchButton: ImageView
    private lateinit var searchField: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var reloadButton: Button
    private lateinit var recyclerTracks: RecyclerView
    private lateinit var historyHeader: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private var trackListSearch = listOf<Track>()
    private var searchAdapter = TrackAdapter { openPlayer(it) }
    private var historyAdapter = TrackAdapter { openPlayer(it) }

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchTracks() }

    private lateinit var tracksInteractor: SearchTracksInteractor

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFS, MODE_PRIVATE) //todo
        searchHistory = SearchHistory(sharedPrefs)
        searchHistory.getSavedHistory()

        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        placeholderMessage = findViewById(R.id.placeholderMessage)
        reloadButton = findViewById(R.id.search_reload_button)
        searchField = findViewById(R.id.search_input_text)
        clearSearchButton = findViewById(R.id.clear_search_button)
        historyHeader = findViewById(R.id.history_header)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        progressBar = findViewById(R.id.progress_bar)

        clearSearchButton.setOnClickListener {//ToDo
            searchField.setText(DEFAULT_TEXT)
            trackListSearch = listOf()
            searchAdapter.notifyDataSetChanged()
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(clearSearchButton.windowToken, 0)
            setHistoryVisibility(true)
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enteredText = s.toString()
                clearSearchButton.visibility = clearButtonVisibility(s)
                recyclerTracks.visibility = View.GONE
                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
                setHistoryVisibility(searchField.hasFocus() && s?.isEmpty() == true)
                searchDebounce()
            }
        })

        searchField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchHistory.getTracks().isNotEmpty()) {
                setHistoryVisibility(hasFocus)
            }
        }

        recyclerTracks = findViewById(R.id.search_recycler)
        recyclerTracks.layoutManager = LinearLayoutManager(this)

        searchAdapter.tracks = trackListSearch
        recyclerTracks.adapter = searchAdapter

        tracksInteractor = Creator.provideTracksInteractor()
        reloadButton.setOnClickListener {
            searchDebounce()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
            setHistoryVisibility(false)
        }
    }

    private fun searchTracks() {
        tracksInteractor.searchTracks(enteredText, object : SearchTracksInteractor.TracksConsumer {
            @SuppressLint("NotifyDataSetChanged")
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    searchAdapter.tracks = foundTracks
                    recyclerTracks.visibility = View.VISIBLE
                    searchAdapter.notifyDataSetChanged()
                }
            }
        })
    }


    fun setHistoryVisibility(searchFieldEmpty: Boolean) {
        if (searchFieldEmpty) {
            historyHeader.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            historyAdapter.tracks = searchHistory.getTracks()
            recyclerTracks.adapter = historyAdapter
            recyclerTracks.visibility = View.VISIBLE
        } else {
            historyHeader.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            historyAdapter.tracks = trackListSearch
            recyclerTracks.adapter = searchAdapter
        }
    }

    override fun onPause() {
        searchHistory.saveHistory()
        super.onPause()
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            searchHistory.addTrackToHistory(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(EXTRA_TRACK, track)
            startActivity(intent)
        }
    }

//    private fun search() {
//        if (enteredText == "") return
//
//        progressBar.visibility = View.VISIBLE
//
//        RetrofitNetworkClient.apiService.search(enteredText)
//            .enqueue(object : Callback<TracksResponse> {
//                override fun onResponse(
//                    call: Call<TracksResponse>,
//                    response: Response<TracksResponse>
//                ) {
//                    progressBar.visibility = View.GONE
//                    recyclerTracks.visibility = View.VISIBLE
//                    setTracks(response)
//                }
//
//                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
//                    progressBar.visibility = View.GONE
//                    setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
//                }
//            })
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun setTracks(response: Response<TracksResponse>) {
//        when (response.code()) {
//            200 -> {
//                if (response.body()?.results?.isNotEmpty() == true) {
//                    trackListSearch.clear()
//                    trackListSearch.addAll(response.body()?.results!!)
//                    searchAdapter.notifyDataSetChanged()
//                    setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
//                } else
//                    setPlaceHolder(PlaceholderMessage.MESSAGE_NOT_FOUND)
//            }
//
//            else -> setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
//        }
//    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setPlaceHolder(message: PlaceholderMessage) {
        when (message) {
            PlaceholderMessage.MESSAGE_CLEAR -> {
                placeholderMessage.visibility = View.GONE
                reloadButton.visibility = View.GONE
            }

            PlaceholderMessage.MESSAGE_NO_INTERNET,
            PlaceholderMessage.MESSAGE_NOT_FOUND -> {
                placeholderMessage.visibility = View.VISIBLE

                if (message == PlaceholderMessage.MESSAGE_NO_INTERNET)
                    reloadButton.visibility = View.VISIBLE
                else reloadButton.visibility = View.GONE

                trackListSearch = listOf()
                searchAdapter.notifyDataSetChanged()
                placeholderMessage.text = this.getString(message.resText)
                placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(0, message.image, 0, 0)
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

        if (enteredText != "") {
            searchField.setText(enteredText)
            //search(enteredText)
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