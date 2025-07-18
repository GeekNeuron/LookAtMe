package com.geekneuron.lookatme

import android.content.Context
import java.util.Locale

object ContextUtils {
    fun updateLocale(context: Context, localeToSwitchTo: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(localeToSwitchTo)
        return context.createConfigurationContext(configuration)
    }
}
