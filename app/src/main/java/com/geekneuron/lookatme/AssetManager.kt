package com.yourcompany.lookatme

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

enum class AssetType {
    FONTS, IMAGES, SOUNDS
}

object AssetManager {

    private const val PREFS_NAME = "LookAtMeUserAssets"

    /**
     * Copies a user-selected file to the app's private internal storage.
     */
    fun copyFileToInternalStorage(context: Context, uri: Uri, assetType: AssetType): File? {
        return try {
            val destinationDir = File(context.filesDir, assetType.name.lowercase())
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }
            val fileName = "${System.currentTimeMillis()}_${getFileName(context, uri)}"
            val destinationFile = File(destinationDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destinationFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Adds a new user asset path to the saved list in SharedPreferences.
     */
    fun addAssetPath(context: Context, assetType: AssetType, filePath: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val assetSet = prefs.getStringSet(assetType.name, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        assetSet.add(filePath)
        prefs.edit().putStringSet(assetType.name, assetSet).apply()
    }

    /**
     * Retrieves the list of all user-imported asset paths for a given type.
     */
    fun getAssetPaths(context: Context, assetType: AssetType): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(assetType.name, setOf())?.toList() ?: emptyList()
    }

    /**
     * Deletes a user asset file from internal storage and its record from SharedPreferences.
     */
    fun deleteAsset(context: Context, assetType: AssetType, filePath: String): Boolean {
        val fileToDelete = File(filePath)
        if (fileToDelete.exists() && !fileToDelete.delete()) {
            return false // Failed to delete the physical file
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val assetSet = prefs.getStringSet(assetType.name, mutableSetOf())?.toMutableSet() ?: return true
        if (assetSet.remove(filePath)) {
            prefs.edit().putStringSet(assetType.name, assetSet).apply()
        }
        return true
    }

    /**
     * Lists all packaged assets from a given R class (e.g., R.font, R.raw) using reflection.
     */
    fun listPackagedAssets(rClass: Class<*>): List<AppAsset.PackagedAsset> {
        val assetList = mutableListOf<AppAsset.PackagedAsset>()
        val fields = rClass.declaredFields
        for (field in fields) {
            try {
                val resourceId = field.getInt(null)
                val resourceName = field.name
                assetList.add(AppAsset.PackagedAsset(resourceName, resourceId))
            } catch (e: Exception) {
                // Ignore fields that are not resources
            }
        }
        return assetList
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result.substring(cut + 1)
            }
        }
        return result?.replace("[^a-zA-Z0-9._-]".toRegex(), "_") ?: "unknown_file"
    }
}
