package com.example.playlistmaker.domain.internalStorage

import android.net.Uri

class InternalStorageInteractorImpl(private val internalStorageRepository: InternalStorageRepository) :
    InternalStorageInteractor {

    override suspend fun saveImage(uri: Uri): String {
        return internalStorageRepository.saveImage(uri)
    }

}