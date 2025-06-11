package com.example.playlistmaker.domain.internalStorage

import android.net.Uri

interface InternalStorageInteractor {
    suspend fun saveImage(uri:Uri):String
}
