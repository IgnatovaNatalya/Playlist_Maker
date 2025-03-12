package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
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
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        binding.settingsToolbar.setNavigationOnClickListener { finish() }

        viewModel.getSavedTheme()

        viewModel.themeState.observe(this) { themeState ->
            binding.themeSwitcher.isChecked = themeState
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.changeTheme(checked)
        }

//        val btnShare = findViewById<TextView>(R.id.settings_share_button)
//        btnShare.setOnClickListener {
//            shareInteractor.shareApp()
//        }
//
//        val btnSupport = findViewById<TextView>(R.id.settings_support_button)
//        btnSupport.setOnClickListener {
//            shareInteractor.sendEmail()
//        }
//
//        val btnAgreement = findViewById<TextView>(R.id.settings_agreement_button)
//        btnAgreement.setOnClickListener {
//            shareInteractor.openTerms()
//        }
    }
}