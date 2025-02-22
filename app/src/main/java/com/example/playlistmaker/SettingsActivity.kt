package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val btnTheme = findViewById<SwitchMaterial>(R.id.theme_switcher)
        btnTheme.isChecked = (applicationContext as App).darkTheme
        btnTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        val btnShare = findViewById<TextView>(R.id.settings_share_button)
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            val chosenIntent = Intent.createChooser(shareIntent, getString(R.string.share_app))
            startActivity(chosenIntent)
        }

        val btnSupport = findViewById<TextView>(R.id.settings_support_button)
        btnSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO). apply {
                data = Uri.parse("mailto:")
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.support_email))
                )
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            }
            startActivity(supportIntent)
        }

        val btnAgreement = findViewById<TextView>(R.id.settings_agreement_button)
        btnAgreement.setOnClickListener {
            val agreementIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_link)))
            startActivity(agreementIntent)
        }
    }
}