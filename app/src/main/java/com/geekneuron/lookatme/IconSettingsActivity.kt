package com.geekneuron.lookatme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geekneuron.lookatme.databinding.ActivityIconSettingsBinding
import com.geekneuron.lookatme.databinding.ItemIconChoiceBinding

// Data class to hold info for each app skin
data class AppSkin(val name: String, val iconResId: Int, val aliasClassName: String)

class IconSettingsActivity : BaseSettingsActivity() {

    private lateinit var binding: ActivityIconSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }
    private lateinit var adapter: IconAdapter

    // Define all your available app skins here
    private val appSkins = listOf(
        AppSkin("Default", R.mipmap.ic_launcher, ".DefaultAlias"),
        AppSkin("Red", R.mipmap.ic_launcher_red, ".RedAlias")
        // Add more skins here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIconSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentAlias = prefs.getString("CURRENT_APP_ALIAS", ".DefaultAlias")
        
        adapter = IconAdapter(appSkins, currentAlias) { selectedSkin ->
            setAppAppearance(selectedSkin.aliasClassName)
            adapter.updateSelection(selectedSkin.aliasClassName)
        }
        
        binding.recyclerViewIcons.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewIcons.adapter = adapter
    }

    private fun setAppAppearance(aliasName: String) {
        val pm = packageManager
        val fullPackageName = packageName

        appSkins.forEach { skin ->
            val componentName = ComponentName(fullPackageName, fullPackageName + skin.aliasClassName)
            val state = if (skin.aliasClassName == aliasName) {
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


// --- RecyclerView Adapter for the icon list ---
class IconAdapter(
    private val skins: List<AppSkin>,
    private var selectedAlias: String?,
    private val onItemClicked: (AppSkin) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(skins[position])
    }

    override fun getItemCount(): Int = skins.size

    fun updateSelection(newSelectedAlias: String) {
        selectedAlias = newSelectedAlias
        notifyDataSetChanged()
    }

    inner class IconViewHolder(private val binding: ItemIconChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(skin: AppSkin) {
            binding.iconPreview.setImageResource(skin.iconResId)
            binding.iconName.text = skin.name
            binding.iconRadioButton.isChecked = (skin.aliasClassName == selectedAlias)
            
            itemView.setOnClickListener {
                onItemClicked(skin)
            }
        }
    }
}
