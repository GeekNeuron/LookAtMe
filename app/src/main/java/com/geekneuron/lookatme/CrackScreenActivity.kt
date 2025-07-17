package com.yourcompany.lookatme

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yourcompany.lookatme.databinding.ActivityCrackScreenBinding

class CrackScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrackScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrackScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val screenshotPath = intent.getStringExtra("SCREENSHOT_PATH")
        if (screenshotPath != null) {
            binding.screenshotImageView.setImageURI(Uri.parse(screenshotPath))
        }

        val crackImages = listOf(R.drawable.crack_1, R.drawable.crack_2, R.drawable.crack_3)
        binding.crackOverlayImageView.setImageResource(crackImages.random())

        binding.root.setOnClickListener {
            finish() // Close on tap
        }
    }
}
