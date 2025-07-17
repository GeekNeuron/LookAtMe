package com.yourcompany.lookatme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yourcompany.lookatme.databinding.ActivityImageSettingsBinding

class ImageSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.imagePreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val imageUriString = prefs.getString("CUSTOM_IMAGE_URI", null)
        if (imageUriString != null) {
            selectedImageUri = Uri.parse(imageUriString)
            Glide.with(this).load(selectedImageUri).into(binding.imagePreview)
        }
        binding.seekBarX.progress = prefs.getInt("IMAGE_POSITION_X_PERCENT", 50)
        binding.seekBarY.progress = prefs.getInt("IMAGE_POSITION_Y_PERCENT", 50)
        updateImagePosition()
    }

    private fun setupListeners() {
        binding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        val seekbarListener = object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateImagePosition()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        binding.seekBarX.setOnSeekBarChangeListener(seekbarListener)
        binding.seekBarY.setOnSeekBarChangeListener(seekbarListener)

        binding.btnSaveImageSettings.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun updateImagePosition() {
        val parentWidth = binding.previewContainer.width
        val parentHeight = binding.previewContainer.height

        if (parentWidth > 0 && parentHeight > 0) {
            binding.imagePreview.translationX = (parentWidth - binding.imagePreview.width) * (binding.seekBarX.progress / 100f)
            binding.imagePreview.translationY = (parentHeight - binding.imagePreview.height) * (binding.seekBarY.progress / 100f)
        }
    }

    private fun saveSettings() {
        prefs.edit().apply {
            putString("CUSTOM_IMAGE_URI", selectedImageUri?.toString())
            putInt("IMAGE_POSITION_X_PERCENT", binding.seekBarX.progress)
            putInt("IMAGE_POSITION_Y_PERCENT", binding.seekBarY.progress)
            apply()
        }
        Toast.makeText(this, "Image Settings Saved!", Toast.LENGTH_SHORT).show()
    }
}
