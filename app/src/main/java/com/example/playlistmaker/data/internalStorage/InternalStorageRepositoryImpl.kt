package com.example.playlistmaker.data.internalStorage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import android.net.Uri
import com.example.playlistmaker.domain.internalStorage.InternalStorageRepository
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class InternalStorageRepositoryImpl(private val context: Context) : InternalStorageRepository {
    override suspend fun saveImage(uri: Uri): String {
        val file = File(context.getExternalFilesDir(DIRECTORY_COVERS), generateUniqueFileName())
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input).compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }
        return file.path
    }

    private fun generateUniqueFileName(extension: String = "png") =
        "${UUID.randomUUID()}.$extension"

    companion object {
        const val DIRECTORY_COVERS = "playlists_covers"
    }
}