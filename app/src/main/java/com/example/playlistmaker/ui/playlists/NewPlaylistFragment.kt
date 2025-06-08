package com.example.playlistmaker.ui.playlists

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.viewmodel.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {

    private lateinit var playlistTitleInput: EditText
    private lateinit var textWatcher: TextWatcher
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private val viewModel: NewPlaylistViewModel by viewModel()
    private var imageIsLoaded = false

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?)
            : FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, requireActivity().resources.displayMetrics
        )

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    imageIsLoaded = true
                    Glide.with(this)
                        .asBitmap()
                        .load(uri)
                        .centerCrop()
                        .into(object : BitmapImageViewTarget(binding.playlistImage) {
                            override fun setResource(resource: Bitmap?) {
                                if (resource == null) return
                                val rounded =
                                    RoundedBitmapDrawableFactory.create(resources, resource)
                                        .apply { cornerRadius = radiusPx }
                                binding.playlistImage.setImageDrawable(rounded)
                            }
                        })
                    saveImageToPrivateStorage(uri)
                }
            }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.playlistTitle.isActivated = false
                    binding.playlistDescription.isActivated = false
                    binding.btnCreate.isEnabled = false
                } else {
                    binding.playlistTitle.isActivated = true
                    binding.playlistDescription.isActivated = true
                    binding.btnCreate.isEnabled = true
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishNotSave()
                }
            })

        playlistTitleInput = binding.playlistTitle
        playlistTitleInput.addTextChangedListener(textWatcher)

        binding.playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistToolbar.setOnClickListener {
            confirmDialog.show()
        }

        binding.btnCreate.setOnClickListener {
            if (binding.btnCreate.isEnabled)
                finishAndSave()
        }
    }

    private fun finishAndSave() {
        viewModel.createPlaylist(
            Playlist(
                0,
                binding.playlistTitle.text.toString(),
                binding.playlistDescription.text.toString(),
                "",
                0,
                emptyList()
            )
        )

        Toast.makeText(
            requireContext(),
            "Плейлист ${binding.playlistTitle.text} создан",
            Toast.LENGTH_LONG
        ).show()
        val rootNavController = requireActivity().findNavController(R.id.fragment_container)
        rootNavController.popBackStack()
    }

    private fun finishNotSave() {
        val rootNavController = requireActivity().findNavController(R.id.fragment_container)

        if (binding.playlistTitle.text.toString() != "" || binding.playlistDescription.text.toString() != "" || imageIsLoaded) {

            confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creating)
                .setMessage(R.string.info_erase_input)
                .setNeutralButton(R.string.cancel) { dialog, which -> }
                .setPositiveButton(R.string.finish) { dialog, which -> rootNavController.popBackStack() }
            confirmDialog.show()
        } else
            rootNavController.popBackStack()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {

        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "first_cover.jpg")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher.let { playlistTitleInput.removeTextChangedListener(it) }
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

}