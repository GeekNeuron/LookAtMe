package com.yourcompany.lookatme

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

object SecurityCheck {
    private const val OFFICIAL_SIGNATURE_HASH = "YOUR_RELEASE_SIGNATURE_HASH_HERE"
    private const val OFFICIAL_PACKAGE_NAME = "com.yourcompany.lookatme"

    fun isTampered(context: Context): Boolean {
        if (context.packageName != OFFICIAL_PACKAGE_NAME) return true
        
        val currentSignatureHash = getSignatureHash(context)
        // Log.d("SecurityCheck", "Current Hash: $currentSignatureHash") // Use this to find your hash
        if (currentSignatureHash == null || currentSignatureHash != OFFICIAL_SIGNATURE_HASH) {
            // return true // Enable this for production
        }
        return false // Disable check for debugging
    }

    private fun getSignatureHash(context: Context): String? {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures.isNotEmpty()) {
                val signature = signatures[0]
                val messageDigest = MessageDigest.getInstance("SHA-256")
                messageDigest.update(signature.toByteArray())
                return Base64.encodeToString(messageDigest.digest(), Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
