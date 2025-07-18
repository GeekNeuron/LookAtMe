package com.geekneuron.lookatme

/**
 * A sealed class to represent both types of assets in a unified way.
 * @param name The display name of the asset.
 */
sealed class AppAsset(open val name: String) {

    /**
     * Represents a packaged resource included in the APK.
     * @param name The resource name (e.g., "my_custom_font").
     * @param resourceId The unique ID from the R class (e.g., R.font.my_custom_font).
     */
    data class PackagedAsset(override val name: String, val resourceId: Int) : AppAsset(name)

    /**
     * Represents a file imported by the user and copied to the app's internal storage.
     * @param name The original filename.
     * @param filePath The absolute path to the file in the app's internal storage.
     */
    data class UserAsset(override val name: String, val filePath: String) : AppAsset(name)
}
