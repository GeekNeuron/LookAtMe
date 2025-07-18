package com.yourcompany.lookatme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.yourcompany.lookatme.databinding.ActivitySoundSettingsBinding

class SoundSettingsActivity : BaseSettingsActivity() {

    private lateinit var binding: ActivitySoundSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    private val selectSoundLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                prefs.edit().putString("CUSTOM_SOUND_URI", it.toString()).apply()
                Toast.makeText(this, "Sound Selected!", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to get permission for the audio file.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoundSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        binding.editDelay.setText(prefs.getInt("SOUND_DELAY_SECONDS", 0).toString())
        binding.editRepeatCount.setText(prefs.getInt("SOUND_REPEAT_COUNT", 1).toString())

        when (prefs.getString("SOUND_LOOP_MODE", "ONCE")) {
            "LOOP" -> binding.radioLoop.isChecked = true
            "REPEAT_N" -> binding.radioRepeatN.isChecked = true
            else -> binding.radioOnce.isChecked = true
        }
        updateRepeatCountVisibility()
    }

    private fun setupListeners() {
        binding.btnSelectSound.setOnClickListener {
            selectSoundLauncher.launch("audio/*")
        }

        binding.radioGroupLoop.setOnCheckedChangeListener { _, checkedId ->
            updateRepeatCountVisibility()
        }

        binding.btnSaveSoundSettings.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun updateRepeatCountVisibility() {
        if (binding.radioRepeatN.isChecked) {
            binding.editRepeatCount.visibility = View.VISIBLE
        } else {
            binding.editRepeatCount.visibility = View.GONE
        }
    }

    private fun saveSettings() {
        val loopMode = when (binding.radioGroupLoop.checkedRadioButtonId) {
            R.id.radio_loop -> "LOOP"
            R.id.radio_repeat_n -> "REPEAT_N"
            else -> "ONCE"
        }
        
        prefs.edit().apply {
            putInt("SOUND_DELAY_SECONDS", binding.editDelay.text.toString().toIntOrNull() ?: 0)
            putString("SOUND_LOOP_MODE", loopMode)
            putInt("SOUND_REPEAT_COUNT", binding.editRepeatCount.text.toString().toIntOrNull() ?: 1)
            apply()
        }
        Toast.makeText(this, "Sound Settings Saved!", Toast.LENGTH_SHORT).show()
    }
}
