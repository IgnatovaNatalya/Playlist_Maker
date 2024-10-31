package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

       ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSearch = findViewById<Button>(R.id.main_search_button)
        val btnMedia = findViewById<Button>(R.id.main_media_button)
        val btnSettings = findViewById<Button>(R.id.main_settings_button)


        btnSearch.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }

        btnMedia.setOnClickListener {
            startActivity(Intent(this,MediaActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
    }
}