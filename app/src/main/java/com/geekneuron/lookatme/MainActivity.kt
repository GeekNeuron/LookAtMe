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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        // Security Check First
        if (SecurityCheck.isTampered(this)) {
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
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

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
            val textScale = prefs.getFloat("TEXT_SCALE", 1.0f)
            binding.titleText.scaleX = textScale
            binding.titleText.scaleY = textScale
            binding.titleText.rotation = prefs.getFloat("TEXT_ROTATION", 0f)
            loadAndApplyFont()
        } else {
            binding.titleText.visibility = View.GONE
        }

        // Image Settings
        if (prefs.getBoolean("IS_IMAGE_ENABLED", false)) {
            binding.centerImage.visibility = View.VISIBLE
            val imageIdentifier = prefs.getString("ACTIVE_IMAGE_IDENTIFIER", null)
            var loadImage: Any? = null
            if (imageIdentifier != null) {
                when {
                    imageIdentifier.startsWith("res:") -> loadImage = imageIdentifier.substringAfter("res:").toInt()
                    imageIdentifier.startsWith("file:") -> loadImage = Uri.parse("file://${imageIdentifier.substringAfter("file:")}")
                }
            }
            Glide.with(this).load(loadImage).into(binding.centerImage)

            binding.root.post {
                val parentWidth = binding.root.width
                val parentHeight = binding.root.height
                binding.centerImage.translationX = (parentWidth - binding.centerImage.width) * (prefs.getInt("IMAGE_POSITION_X_PERCENT", 50) / 100f)
                binding.centerImage.translationY = (parentHeight - binding.centerImage.height) * (prefs.getInt("IMAGE_POSITION_Y_PERCENT", 50) / 100f)
            }

            val imageScale = prefs.getFloat("IMAGE_SCALE", 1.0f)
            binding.centerImage.scaleX = imageScale
            binding.centerImage.scaleY = imageScale
            binding.centerImage.rotation = prefs.getFloat("IMAGE_ROTATION", 0f)
        } else {
            binding.centerImage.visibility = View.GONE
        }

        // Sound Settings
        if (prefs.getBoolean("IS_SOUND_ENABLED", false)) {
            setupAndPlaySoundFromPrefs()
        }
    }
    
    private fun loadAndApplyFont() {
        val fontIdentifier = prefs.getString("ACTIVE_FONT_IDENTIFIER", null)
        var customTypeface: Typeface? = null
        if (fontIdentifier != null) {
            try {
                when {
                    fontIdentifier.startsWith("res:") -> {
                        val resourceId = fontIdentifier.substringAfter("res:").toInt()
                        customTypeface = ResourcesCompat.getFont(this, resourceId)
                    }
                    fontIdentifier.startsWith("file:") -> {
                        val filePath = fontIdentifier.substringAfter("file:")
                        customTypeface = Typeface.createFromFile(File(filePath))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.titleText.typeface = customTypeface ?: Typeface.DEFAULT
    }

    private fun unlockScreen() {
        if (binding.lockView.visibility == View.VISIBLE) {
            triggerVibration(200)
            binding.lockView.animate().alpha(0f).setDuration(500).withEndAction {
                binding.lockView.visibility = View.GONE
                enableScreenPinning()
                triggerCrackEffectIfNeeded()
            }.start()
        }
    }

    private fun enableScreenPinning() {
        startLockTask()
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
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun triggerVibration(milliseconds: Long) {
        if (prefs.getBoolean("IS_VIBRATION_ENABLED", true)) {
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
        val soundIdentifier = prefs.getString("ACTIVE_SOUND_IDENTIFIER", null) ?: return
        mediaPlayer?.release()
        
        try {
            mediaPlayer = MediaPlayer().apply {
                when {
                    soundIdentifier.startsWith("res:") -> {
                        val resId = soundIdentifier.substringAfter("res:").toInt()
                        val afd = resources.openRawResourceFd(resId)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    }
                    soundIdentifier.startsWith("file:") -> {
                        val filePath = soundIdentifier.substringAfter("file:")
                        setDataSource(filePath)
                    }
                }
                
                val loopMode = prefs.getString("SOUND_LOOP_MODE", "ONCE")
                isLooping = (loopMode == "LOOP")
                
                prepareAsync()
            }
            
            mediaPlayer?.setOnPreparedListener { mp ->
                val delay = prefs.getInt("SOUND_DELAY_SECONDS", 0) * 1000L
                Handler(Looper.getMainLooper()).postDelayed({ mp.start() }, delay)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSettingsDialog() {
        SettingsDialogFragment().show(supportFragmentManager, "SettingsDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        unlockHandler.removeCallbacksAndMessages(null)
        settingsHandler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
