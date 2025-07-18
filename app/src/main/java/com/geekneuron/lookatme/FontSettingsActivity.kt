package com.yourcompany.lookatme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.yourcompany.lookatme.databinding.ActivityFontSettingsBinding

class FontSettingsActivity : BaseSettingsActivity() {

    private lateinit var binding: ActivityFontSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    private val selectFontLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                // Persist permission to read the URI across device reboots
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                prefs.edit().putString("CUSTOM_FONT_URI", it.toString()).apply()
                Toast.makeText(this, "Font Selected!", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to get permission for the font file.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFontSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        binding.editTextTitle.setText(prefs.getString("CUSTOM_TEXT", "Look At Me"))
        binding.seekBarFontSize.progress = prefs.getInt("CUSTOM_TEXT_SIZE", 34)
        // Convert scale (0.5-1.5) to progress (0-100)
        binding.seekBarScale.progress = ((prefs.getFloat("TEXT_SCALE", 1.0f) - 0.5f) * 100).toInt()
        binding.seekBarRotation.progress = prefs.getFloat("TEXT_ROTATION", 0f).toInt()
    }

    private fun setupListeners() {
        binding.btnSelectFont.setOnClickListener {
            selectFontLauncher.launch("font/*") // More specific MIME type
        }

        binding.btnSaveFontSettings.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun saveSettings() {
        prefs.edit().apply {
            putString("CUSTOM_TEXT", binding.editTextTitle.text.toString())
            putInt("CUSTOM_TEXT_SIZE", binding.seekBarFontSize.progress)
            // Convert progress (0-100) back to scale (0.5-1.5)
            putFloat("TEXT_SCALE", 0.5f + (binding.seekBarScale.progress / 100f))
            putFloat("TEXT_ROTATION", binding.seekBarRotation.progress.toFloat())
            apply()
        }
        Toast.makeText(this, "Text Settings Saved!", Toast.LENGTH_SHORT).show()
    }
}
