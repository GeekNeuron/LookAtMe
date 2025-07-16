package com.example.lookatme

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    // companion object for defining constants (equivalent to static final in Java)
    companion object {
        // --- Feature Toggles ---
        private const val IS_TITLE_ENABLED = true
        private const val IS_IMAGE_ENABLED = true
        private const val IS_FOOTER_ENABLED = true
        private const val IS_SCROLLING_TEXT_ENABLED = true
        private const val IS_SOUND_ENABLED = true
        private const val IS_VIBRATION_ENABLED = true

        // --- Image Type ---
        private const val USE_ANIMATED_IMAGE = true // true: GIF, false: PNG

        // --- Unlock Mechanism ---
        private const val UNLOCK_DELAY_SECONDS = 9

        // --- Background Sound ---
        private const val SOUND_DELAY_SECONDS = 2
        private const val SOUND_SHOULD_LOOP = true
    }

    // lateinit for views that will be initialized in onCreate
    private lateinit var lockView: FrameLayout
    private lateinit var scrollingText: TextView
    private lateinit var titleText: TextView
    private lateinit var footerText: TextView
    private lateinit var centerImage: ImageView

    private val unlockHandler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        configureFeatures()
    }

    private fun initializeViews() {
        lockView = findViewById(R.id.lock_view)
        titleText = findViewById(R.id.title_text)
        centerImage = findViewById(R.id.center_image)
        footerText = findViewById(R.id.footer_text)
        scrollingText = findViewById(R.id.scrolling_text)
    }

    private fun configureFeatures() {
        setupVisualElement(titleText, IS_TITLE_ENABLED)
        setupVisualElement(centerImage, IS_IMAGE_ENABLED)
        setupVisualElement(footerText, IS_FOOTER_ENABLED)
        setupVisualElement(scrollingText, IS_SCROLLING_TEXT_ENABLED)

        setupImageSource()
        setupLongPressUnlock()

        if (IS_SOUND_ENABLED) {
            setupAndPlaySound()
        }
        if (IS_VIBRATION_ENABLED) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun setupVisualElement(view: View, isEnabled: Boolean) {
        if (isEnabled) {
            if (view is TextView) {
                applyCustomFontTo(view)
            }
        } else {
            view.visibility = View.GONE
        }
    }

    private fun setupImageSource() {
        if (IS_IMAGE_ENABLED) {
            if (USE_ANIMATED_IMAGE) {
                Glide.with(this)
                    .asGif()
                    .load(R.drawable.animated_image) // Your GIF file
                    .into(centerImage)
            } else {
                centerImage.setImageResource(R.drawable.your_png_image) // Your PNG file
            }
        }
    }

    private val unlockRunnable = Runnable { unlockScreen() }

    private fun setupLongPressUnlock() {
        // Using a lambda for cleaner, more readable code
        lockView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    unlockHandler.postDelayed(unlockRunnable, UNLOCK_DELAY_SECONDS * 1000L)
                    triggerVibration(50) // Short vibration on press
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    unlockHandler.removeCallbacks(unlockRunnable) // Cancel if finger is lifted
                }
            }
            true // Return true to consume the event
        }
    }

    private fun setupAndPlaySound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound)
        mediaPlayer?.let {
            it.isLooping = SOUND_SHOULD_LOOP
            Handler(Looper.getMainLooper()).postDelayed({
                it.start()
            }, SOUND_DELAY_SECONDS * 1000L)
        }
    }

    private fun applyCustomFontTo(textView: TextView) {
        try {
            val customFont: Typeface? = ResourcesCompat.getFont(this, R.font.my_custom_font)
            textView.typeface = customFont
        } catch (e: Exception) {
            // Font not found, do nothing
        }
    }

    private fun unlockScreen() {
        if (lockView.visibility == View.VISIBLE) {
            triggerVibration(200) // Longer vibration on successful unlock
            lockView.animate().alpha(0f).setDuration(500).withEndAction {
                lockView.visibility = View.GONE
                if (IS_SCROLLING_TEXT_ENABLED) {
                    startScrollingAnimation()
                }
            }.start()
        }
    }

    private fun startScrollingAnimation() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels.toFloat()

        scrollingText.translationY = -200f // Start off-screen
        scrollingText.visibility = View.VISIBLE

        val animator = ObjectAnimator.ofFloat(scrollingText, "translationY", -200f, height)
        animator.duration = 15000L // 15 seconds
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.start()
    }

    private fun triggerVibration(milliseconds: Long) {
        // Using a null-safe call (?.) for simplicity
        vibrator?.let {
            if (it.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(milliseconds)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources to prevent memory leaks
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        // Remove any pending callbacks to prevent leaks
        unlockHandler.removeCallbacks(unlockRunnable)
    }
}
