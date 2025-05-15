package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSavedTheme()

        viewModel.themeState.observe(viewLifecycleOwner) { themeState ->
            binding.themeSwitcher.isChecked = themeState
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.changeTheme(checked)
        }

        binding.settingsShareButton.setOnClickListener{
            viewModel.shareApp()
        }

        binding.settingsSupportButton.setOnClickListener{
            viewModel.callSupport()
        }

        binding.settingsAgreementButton.setOnClickListener{
            viewModel.openAgreement()
        }
    }
}