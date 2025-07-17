package com.yourcompany.lookatme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.lookatme.databinding.ActivityIconSettingsBinding

class IconSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIconSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIconSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // In a real app, you would use a RecyclerView to show all options.
        // This is a simplified example.
        binding.btnSetDefaultIcon.setOnClickListener {
            setAppAppearance(".DefaultAlias")
        }
        binding.btnSetRedIcon.setOnClickListener {
            setAppAppearance(".RedAlias")
        }
    }

    private fun setAppAppearance(aliasName: String) {
        val pm = packageManager
        val fullPackageName = packageName

        val aliases = listOf(".DefaultAlias", ".RedAlias") // Add all your aliases here

        aliases.forEach { alias ->
            val componentName = ComponentName(fullPackageName, fullPackageName + alias)
            val state = if (alias == aliasName) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            pm.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP)
        }
        prefs.edit().putString("CURRENT_APP_ALIAS", aliasName).apply()
        Toast.makeText(this, "App icon will change shortly!", Toast.LENGTH_SHORT).show()
    }
}
