package com.yourcompany.lookatme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.yourcompany.lookatme.databinding.FragmentSettingsBinding

class SettingsDialogFragment : DialogFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val prefs by lazy { requireActivity().getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCheckboxStates()
        setupListeners()
    }

    private fun loadCheckboxStates() {
        binding.checkTextSettings.isChecked = prefs.getBoolean("IS_TEXT_ENABLED", false)
        binding.checkImageSettings.isChecked = prefs.getBoolean("IS_IMAGE_ENABLED", false)
        binding.checkSoundSettings.isChecked = prefs.getBoolean("IS_SOUND_ENABLED", false)
        binding.checkVibration.isChecked = prefs.getBoolean("IS_VIBRATION_ENABLED", false)
        binding.checkCrackEffect.isChecked = prefs.getBoolean("IS_CRACK_EFFECT_ENABLED", false)
    }

    private fun setupListeners() {
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
        binding.btnIconSettings.setOnClickListener {
            startActivity(Intent(activity, IconSettingsActivity::class.java))
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
