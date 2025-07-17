package com.yourcompany.lookatme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.lookatme.databinding.ActivitySoundSettingsBinding

class SoundSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySoundSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    private val selectSoundLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            prefs.edit().putString("CUSTOM_SOUND_URI", it.toString()).apply()
            Toast.makeText(this, "Sound Selected!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoundSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectSound.setOnClickListener {
            selectSoundLauncher.launch("audio/*")
        }

        binding.btnSaveSoundSettings.setOnClickListener {
            prefs.edit().apply {
                putInt("SOUND_DELAY_SECONDS", binding.editDelay.text.toString().toIntOrNull() ?: 0)
                // Save other sound settings
                apply()
            }
            Toast.makeText(this, "Sound Settings Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
