package com.geekneuron.lookatme

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekneuron.lookatme.databinding.ActivityFontSettingsBinding // فرض کنید لایه شما یک RecyclerView و یک Button دارد
import java.io.File

class FontSettingsActivity : BaseSettingsActivity() {

    private lateinit var binding: ActivityFontSettingsBinding
    private val prefs by lazy { getSharedPreferences("LookAtMeSettings", Context.MODE_PRIVATE) }
    private lateinit var assetAdapter: AssetAdapter

    private val selectFontLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val copiedFile = AssetManager.copyFileToInternalStorage(this, it, AssetType.FONTS)
            if (copiedFile != null) {
                AssetManager.addAssetPath(this, AssetType.FONTS, copiedFile.absolutePath)
                Toast.makeText(this, "Font added successfully!", Toast.LENGTH_SHORT).show()
                loadAllAvailableFonts() // Refresh the list
            } else {
                Toast.makeText(this, "Failed to add font.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFontSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        assetAdapter = AssetAdapter(
            assetType = AssetType.FONTS,
            onAssetSelected = { selectedAsset ->
                val identifier = when (selectedAsset) {
                    is AppAsset.PackagedAsset -> "res:${selectedAsset.resourceId}"
                    is AppAppAsset.UserAsset -> "file:${selectedAsset.filePath}"
                }
                prefs.edit().putString("ACTIVE_FONT_IDENTIFIER", identifier).apply()
                Toast.makeText(this, "${selectedAsset.name} selected.", Toast.LENGTH_SHORT).show()
                finish()
            },
            onAssetPlay = { /* Not needed for fonts */ },
            onAssetDelete = { assetToDelete ->
                if (assetToDelete is AppAsset.UserAsset) {
                    if (AssetManager.deleteAsset(this, AssetType.FONTS, assetToDelete.filePath)) {
                        Toast.makeText(this, "Font deleted.", Toast.LENGTH_SHORT).show()
                        loadAllAvailableFonts()
                    }
                }
            }
        )
        binding.recyclerViewFonts.adapter = assetAdapter
        binding.recyclerViewFonts.layoutManager = LinearLayoutManager(this)
        loadAllAvailableFonts()
    }

    private fun loadAllAvailableFonts() {
        val packagedFonts = AssetManager.listPackagedAssets(R.font::class.java)
        val userFontPaths = AssetManager.getAssetPaths(this, AssetType.FONTS)
        val userFonts = userFontPaths.map { path ->
            val file = File(path)
            AppAsset.UserAsset(file.nameWithoutExtension, path)
        }
        assetAdapter.submitList(packagedFonts + userFonts)
    }

    private fun setupListeners() {
        binding.btnAddFont.setOnClickListener {
            selectFontLauncher.launch("font/*")
        }
    }
}
