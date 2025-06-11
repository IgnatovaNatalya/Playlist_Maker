package com.example.playlistmaker.domain.internalStorage

import android.content.Context
import android.net.Uri

interface InternalStorageRepository {
    suspend fun saveImage(uri: Uri): String
}