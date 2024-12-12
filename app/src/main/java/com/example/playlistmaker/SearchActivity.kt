package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var clearButton: ImageView
    private lateinit var searchField: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var reloadButton: Button
    private lateinit var recyclerTracks: RecyclerView

    private val trackList = ArrayList<Track>()
    private var trackAdapter = TrackAdapter()


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

        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        placeholderMessage = findViewById(R.id.placeholderMessage)
        reloadButton = findViewById(R.id.search_reload_button)

        searchField = findViewById(R.id.search_input_text)

        clearButton = findViewById(R.id.search_clear_button)

        clearButton.setOnClickListener {
            searchField.setText(DEFAULT_TEXT)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enteredText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
            }
        })

        //recycler
        recyclerTracks = findViewById(R.id.search_recycler)
        recyclerTracks.layoutManager = LinearLayoutManager(this)

        trackAdapter.tracks = trackList
        recyclerTracks.adapter = trackAdapter

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchField.text.toString())
            }
            false
        }
        reloadButton.setOnClickListener( {
            search(enteredText)
        })
    }

    private fun search(request: String) {

        RetrofitInstance.apiService.search(request)
            .enqueue(object : Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.clear()
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                setPlaceHolder(PlaceholderMessage.MESSAGE_CLEAR)
                            } else {
//                                trackList.clear()
//                                trackAdapter.notifyDataSetChanged()
                                setPlaceHolder(PlaceholderMessage.MESSAGE_NOT_FOUND)
                            }
                        }

                        else -> setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    setPlaceHolder(PlaceholderMessage.MESSAGE_NO_INTERNET)
                }

            })
    }

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
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                placeholderMessage.text = message.text
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
        searchField.setText(enteredText)
        search(enteredText)
    }

    private var enteredText: String = DEFAULT_TEXT

    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val DEFAULT_TEXT = ""
    }
}

