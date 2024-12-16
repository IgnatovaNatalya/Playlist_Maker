package com.example.playlistmaker

import androidx.annotation.StringRes

enum class PlaceholderMessage(@StringRes val resText: Int, val image:Int) {

    MESSAGE_NO_INTERNET(R.string.no_internet, R.drawable.no_internet),
    MESSAGE_NOT_FOUND (R.string.not_found, R.drawable.not_found),
    MESSAGE_CLEAR(0,0)
}