package com.yourcompany.lookatme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

abstract class BaseSettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Reads the locale code from the BuildConfig field set by the Product Flavor
        val forcedLocaleCode = BuildConfig.FORCED_LOCALE
        val localeToSwitchTo = Locale(forcedLocaleCode)
        val localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }
}
