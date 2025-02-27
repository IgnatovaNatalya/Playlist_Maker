package com.example.playlistmaker.presentation.settings

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import com.example.playlistmaker.domain.interactor.ShareInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeInteractor: ThemeInteractor
    private lateinit var shareInteractor: ShareInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeInteractor = Creator.provideThemeInteractor(this)
        shareInteractor = Creator.provideShareInteractor(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val btnTheme = findViewById<SwitchMaterial>(R.id.theme_switcher)

        btnTheme.isChecked = themeInteractor.getSavedTheme()
        btnTheme.setOnCheckedChangeListener { _, checked ->
            themeInteractor.switchTheme(checked)
        }

        val btnShare = findViewById<TextView>(R.id.settings_share_button)
        btnShare.setOnClickListener {
            shareInteractor.sendLink()
        }

        val btnSupport = findViewById<TextView>(R.id.settings_support_button)
        btnSupport.setOnClickListener {
            shareInteractor.sendEmail()
        }

        val btnAgreement = findViewById<TextView>(R.id.settings_agreement_button)
        btnAgreement.setOnClickListener {
            shareInteractor.openLink()
        }
    }
}