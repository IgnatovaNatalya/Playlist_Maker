package com.example.playlistmaker.ui.newPlaylist

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.NewPlaylistUiState
import com.example.playlistmaker.viewmodel.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {

    private lateinit var playlistTitleInput: EditText
    private lateinit var textWatcher: TextWatcher
    private var isEdit = false
    var id: Int? = null

    private val viewModel: NewPlaylistViewModel by viewModel {
        id = if (arguments != null) requireArguments().getInt(EXTRA_PLAYLIST_ID) else null
        parametersOf(id)
    }
    private var imageUri: Uri? = null

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNewPlaylistBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) isEdit = true

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { handleImageSelected(it) }
        }

        viewModel.newPlaylistsState.observe(viewLifecycleOwner) { state ->
            if (state is NewPlaylistUiState.Exist)
                drawPlaylist(state.playlist)
        }

        setupTextWatcher()

        viewModel.playlistCreated.observe(viewLifecycleOwner) {
            if (it == true) {
                val message = if (isEdit) "Плейлист сохранен" else "Плейлист создан"
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                requireActivity().findNavController(R.id.fragment_container).popBackStack()
            }
        }

        binding.playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.playlistToolbar.setOnClickListener { finishNotSave() }
        binding.btnCreate.setOnClickListener { createPlaylist() }

        requireActivity().onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishNotSave()
                }
            })
    }

    private fun drawPlaylist(playlist: Playlist) {
        binding.playlistToolbar.title = getString(R.string.edit)
        val uri = playlist.path.toUri()
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics
        )
        imageUri = uri
        loadRoundedImage(uri, radiusPx)

        binding.playlistTitle.setText(playlist.title)
        binding.playlistDescription.setText(playlist.description)
        binding.btnCreate.text = "Сохранить"

    }

    private fun createPlaylist() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.createPlaylist(
            binding.playlistTitle.text.toString(),
            binding.playlistDescription.text.toString(),
            imageUri?.toString() ?: "",
        )
    }

    private fun setupTextWatcher() {
        textWatcher = object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnCreate.isEnabled = !s.isNullOrEmpty()
            }
        }
        playlistTitleInput = binding.playlistTitle
        playlistTitleInput.addTextChangedListener(textWatcher)
    }

    private fun handleImageSelected(uri: Uri) {
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics
        )
        loadRoundedImage(uri, radiusPx)
        viewModel.saveImage(uri).also { imageUri = uri }
    }

    private fun loadRoundedImage(uri: Uri, radiusPx: Float) {
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.album_placeholder)
            .into(object : BitmapImageViewTarget(binding.playlistImage) {
                override fun setResource(resource: Bitmap?) {
                    resource?.let {
                        val rounded = RoundedBitmapDrawableFactory.create(resources, it).apply {
                            cornerRadius = radiusPx
                        }
                        binding.playlistImage.setImageDrawable(rounded)
                    }
                }
            })
    }

    private fun finishNotSave() {

        val rootNavController = requireActivity().findNavController(R.id.fragment_container)

        if (isEdit)
            rootNavController.popBackStack()
        else if (binding.playlistTitle.text.toString() != "" || binding.playlistDescription.text.toString() != "" || imageUri!=null) {
            val confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creating)
                .setMessage(R.string.info_erase_input)
                .setNeutralButton(R.string.cancel) { dialog, which -> }
                .setPositiveButton(R.string.finish) { dialog, which -> rootNavController.popBackStack() }
            confirmDialog.show()
        } else
            rootNavController.popBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher.let { playlistTitleInput.removeTextChangedListener(it) }
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "EXTRA_PLAYLIST_ID"

        fun createArgs(playlistId: Int?): Bundle = bundleOf(EXTRA_PLAYLIST_ID to playlistId)
    }
}