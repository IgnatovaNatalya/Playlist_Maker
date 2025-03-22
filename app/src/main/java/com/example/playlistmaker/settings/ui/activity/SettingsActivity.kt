package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(
                Creator.provideSharingInteractor(this),
                Creator.provideThemeInteractor(this)
            )
        )[SettingsViewModel::class.java]

        binding.settingsToolbar.setNavigationOnClickListener { finish() }

        viewModel.getSavedTheme()

        viewModel.themeState.observe(this) { themeState ->
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