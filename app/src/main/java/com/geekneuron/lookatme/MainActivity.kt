package com.yourcompany.lookatme

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yourcompany.lookatme.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val unlockHandler = Handler(Looper.getMainLooper())
    private val settingsHandler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    companion object {
        const val UNLOCK_DELAY_SECONDS = 9
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (SecurityCheck.isTampered(this)) {
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        setupLongPressUnlock()
    }

    override fun onResume() {
        super.onResume()
        loadAndApplyUserSettings()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private val unlockRunnable = Runnable { unlockScreen() }

    private fun setupLongPressUnlock() {
        binding.lockView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val screenWidth = resources.displayMetrics.widthPixels
                    val settingsAreaWidth = screenWidth * 0.2
                    val settingsAreaHeight = 200

                    if (event.x > screenWidth - settingsAreaWidth && event.y < settingsAreaHeight) {
                        settingsHandler.postDelayed({ showSettingsDialog() }, 5000L)
                    } else {
                        unlockHandler.postDelayed(unlockRunnable, UNLOCK_DELAY_SECONDS * 1000L)
                    }
                    triggerVibration(50)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    unlockHandler.removeCallbacks(unlockRunnable)
                    settingsHandler.removeCallbacksAndMessages(null)
                }
            }
            true
        }
    }

    private fun loadAndApplyUserSettings() {
        // Text Settings
        if (prefs.getBoolean("IS_TEXT_ENABLED", false)) {
            binding.titleText.visibility = View.VISIBLE
            binding.titleText.text = prefs.getString("CUSTOM_TEXT", "Title")
            binding.titleText.textSize = prefs.getInt("CUSTOM_TEXT_SIZE", 34).toFloat()
            // ... load other properties
        } else {
            binding.titleText.visibility = View.GONE
        }
        // Image Settings
        if (prefs.getBoolean("IS_IMAGE_ENABLED", false)) {
            binding.centerImage.visibility = View.VISIBLE
            val uriString = prefs.getString("CUSTOM_IMAGE_URI", null)
            uriString?.let { Glide.with(this).load(Uri.parse(it)).into(binding.centerImage) }
            // Apply positioning logic
        } else {
            binding.centerImage.visibility = View.GONE
        }
        // Sound Settings
        if (prefs.getBoolean("IS_SOUND_ENABLED", false)) {
            setupAndPlaySoundFromPrefs()
        }
    }

    private fun unlockScreen() {
        if (binding.lockView.visibility == View.VISIBLE) {
            triggerVibration(200)
            binding.lockView.animate().alpha(0f).setDuration(500).withEndAction {
                binding.lockView.visibility = View.GONE
                triggerCrackEffectIfNeeded()
            }.start()
        }
    }

    private fun triggerCrackEffectIfNeeded() {
        if (prefs.getBoolean("IS_CRACK_EFFECT_ENABLED", false)) {
            val delay = prefs.getInt("CRACK_EFFECT_DELAY", 5) * 1000L
            Handler(Looper.getMainLooper()).postDelayed({
                val screenshotPath = takeScreenshotAndSave()
                screenshotPath?.let {
                    val intent = Intent(this, CrackScreenActivity::class.java).apply {
                        putExtra("SCREENSHOT_PATH", it)
                    }
                    startActivity(intent)
                }
            }, delay)
        }
    }

    private fun takeScreenshotAndSave(): String? {
        return try {
            val rootView = window.decorView.rootView
            rootView.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(rootView.drawingCache)
            rootView.isDrawingCacheEnabled = false
            val file = File(cacheDir, "screenshot.png")
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, it)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun triggerVibration(milliseconds: Long) {
        if (prefs.getBoolean("IS_VIBRATION_ENABLED", false)) {
            val amplitude = prefs.getInt("VIBRATION_AMPLITUDE", 128)
            if (amplitude == 0) return
            vibrator?.let {
                if (it.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(milliseconds)
                    }
                }
            }
        }
    }
    
    private fun setupAndPlaySoundFromPrefs() {
        // Logic to read sound settings from prefs and play media
    }

    private fun showSettingsDialog() {
        SettingsDialogFragment().show(supportFragmentManager, "SettingsDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        unlockHandler.removeCallbacksAndMessages(null)
        settingsHandler.removeCallbacksAndMessages(null)
    }
}
