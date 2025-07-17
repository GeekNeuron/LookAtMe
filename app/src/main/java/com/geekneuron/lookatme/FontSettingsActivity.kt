package com.yourcompany.lookatme

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.lookatme.databinding.ActivityFontSettingsBinding

class FontSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFontSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    private val selectFontLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Persist permission to read the URI
            val takeFlags = contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            prefs.edit().putString("CUSTOM_FONT_URI", it.toString()).apply()
            Toast.makeText(this, "Font Selected!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFontSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()

        binding.btnSelectFont.setOnClickListener {
            selectFontLauncher.launch("*/*")
        }

        binding.btnSaveFontSettings.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun loadSettings() {
        binding.editTextTitle.setText(prefs.getString("CUSTOM_TEXT", "Look At Me"))
        binding.seekBarFontSize.progress = prefs.getInt("CUSTOM_TEXT_SIZE", 34)
    }

    private fun saveSettings() {
        prefs.edit().apply {
            putString("CUSTOM_TEXT", binding.editTextTitle.text.toString())
            putInt("CUSTOM_TEXT_SIZE", binding.seekBarFontSize.progress)
            // Save other settings like color, angle, etc.
            apply()
        }
        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show()
    }
}
