package com.geekneuron.lookatme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.geekneuron.lookatme.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsDialogFragment : DialogFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val prefs by lazy { requireActivity().getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    // This method forces the DialogFragment to use the specified locale
    override fun onAttach(context: Context) {
        val forcedLocaleCode = BuildConfig.FORCED_LOCALE
        val localeToSwitchTo = Locale(forcedLocaleCode)
        super.onAttach(ContextUtils.updateLocale(context, localeToSwitchTo))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        // Load checkbox states
        binding.checkTextSettings.isChecked = prefs.getBoolean("IS_TEXT_ENABLED", false)
        binding.checkImageSettings.isChecked = prefs.getBoolean("IS_IMAGE_ENABLED", false)
        binding.checkSoundSettings.isChecked = prefs.getBoolean("IS_SOUND_ENABLED", false)
        binding.checkVibration.isChecked = prefs.getBoolean("IS_VIBRATION_ENABLED", true)
        binding.checkCrackEffect.isChecked = prefs.getBoolean("IS_CRACK_EFFECT_ENABLED", false)

        // Load SeekBar and EditText values
        binding.seekBarVibration.progress = prefs.getInt("VIBRATION_AMPLITUDE", 128)
        binding.editCrackDelay.setText(prefs.getInt("CRACK_EFFECT_DELAY", 5).toString())
    }

    private fun setupListeners() {
        // --- CheckBox Listeners ---
        binding.checkTextSettings.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("IS_TEXT_ENABLED", isChecked).apply()
            if (isChecked) startActivity(Intent(activity, FontSettingsActivity::class.java))
        }
        binding.checkImageSettings.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("IS_IMAGE_ENABLED", isChecked).apply()
            if (isChecked) startActivity(Intent(activity, ImageSettingsActivity::class.java))
        }
        binding.checkSoundSettings.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("IS_SOUND_ENABLED", isChecked).apply()
            if (isChecked) startActivity(Intent(activity, SoundSettingsActivity::class.java))
        }
        binding.checkVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("IS_VIBRATION_ENABLED", isChecked).apply()
        }
        binding.checkCrackEffect.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("IS_CRACK_EFFECT_ENABLED", isChecked).apply()
        }

        // --- SeekBar Listener for Vibration ---
        binding.seekBarVibration.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                prefs.edit().putInt("VIBRATION_AMPLITUDE", seekBar?.progress ?: 128).apply()
            }
        })

        // --- Button Listeners ---
        binding.btnIconSettings.setOnClickListener {
            startActivity(Intent(activity, IconSettingsActivity::class.java))
        }

        binding.btnSaveCrackDelay.setOnClickListener {
            val delay = binding.editCrackDelay.text.toString().toIntOrNull() ?: 5
            prefs.edit().putInt("CRACK_EFFECT_DELAY", delay).apply()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
