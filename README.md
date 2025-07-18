# LookAtMe

A unique and highly customizable Android application that acts as a transparent overlay on the home screen. It transforms your launcher into a personal, artistic, or fun experience, revealed through a unique long-press interaction. The app is built to be a creative canvas, controlled entirely by the end-user through a hidden, comprehensive settings panel.

---

## ✨ Features

-   **Transparent Overlay:** Runs as a seamless layer on top of the device's launcher.
-   **Hidden Settings Panel:** Access a rich settings menu via a secret long-press gesture.
-   **Full User Customization:** No developer intervention needed. Users can:
    -   Add multiple text elements.
    -   Import custom fonts, images (PNG), and animated GIFs from their device.
    -   Precisely control the **position, scale (size), and rotation** of each text and image element.
    -   Import custom background audio with controls for start delay and looping.
    -   Enable a fun "cracked screen" prank effect with a configurable timer.
    -   Adjust vibration intensity.
-   **Integrated Asset Browser:** Automatically detects and displays developer-packaged assets alongside user-imported files in a unified gallery.
-   **Dynamic App Appearance:** Change the app's home screen icon and name from a predefined list of "skins".
-   **Build Flavors:** Architected with Gradle Product Flavors to easily build separate versions (e.g., for different app stores) with distinct, locked-in settings languages.
-   **Tamper Detection:** Includes a security check to verify the app's signature and prevent unauthorized modification.

---

## 🚀 Getting Started

1.  Clone this repository to your local machine.
2.  Open the project in Android Studio.
3.  Click **Sync Now** when prompted. Gradle will automatically download the required dependencies (like Glide).
4.  Select your desired build variant (e.g., `englishDebug` or `persianDebug`) in the **Build Variants** tool window.
5.  Build and run the app on an emulator or a physical device.

---

## ⚙️ App Customization (User Guide)

All customization is done through a hidden settings panel within the app itself.

-   **To Access Settings:** Press and hold your finger in the **top-right corner** of the screen for **5 seconds**.

From there, you can explore the different menus to import your own fonts, images, and sounds, and control every aspect of your screen's appearance.

---

## 🔧 Developer Configuration Guide

This guide covers the final steps for a developer to prepare the app for release.

### 1. Customizing Pre-packaged Assets

You can pre-package the app with default assets. The internal asset browser will automatically detect them.

| Content Type       | File Location                | Instructions                                 |
| ------------------ | ---------------------------- | -------------------------------------------- |
| **Default Font** | `app/src/main/res/font/`     | Place your default `.ttf` font file here.    |
| **Default Sound** | `app/src/main/res/raw/`      | Place a default `.mp3` or `.ogg` audio file. |
| **Crack Images** | `app/src/main/res/drawable/` | Place `crack_1.png`, `crack_2.png`, etc. here. |
| **App Icon Skins** | `app/src/main/res/mipmap-*`  | Place alternate icon sets (e.g., `ic_launcher_red.webp`). |

### 2. Finalizing App Identity

Before publishing, you must finalize the app's unique identifiers in `app/build.gradle.kts` and the security key in `SecurityCheck.kt`.

#### **Package Name & Version**

In `app/build.gradle.kts`, set your final `applicationId`, `versionCode`, and `versionName`.

```kotlin
// In app/build.gradle.kts
android {
    // ...
    defaultConfig {
        // ⚠️ IMPORTANT: Change this to your unique ID before publishing!
        applicationId = "com.yourcompany.lookatme"
        
        versionCode = 1       // Increment this for every new release
        versionName = "1.0"   // User-facing version string
    }
    // ...
}
```

#### **Security Signature Hash**

The app will not run if it's been tampered with. You must insert your own release signature hash.

1.  Generate a signed release APK using your private keystore (`Build > Generate Signed Bundle / APK...`).
2.  Install this signed APK on a device.
3.  Temporarily uncomment the `Log.d(...)` line in `SecurityCheck.kt` and run the app while connected to Android Studio.
4.  Copy the signature hash printed in the **Logcat** window.
5.  Paste the real hash into the `OFFICIAL_SIGNATURE_HASH` constant in `SecurityCheck.kt`.
6.  Re-comment the `Log.d` line and re-enable the security check.

```kotlin
// In SecurityCheck.kt
object SecurityCheck {
    // ⚠️ Replace with your actual signature hash and package name
    private const val OFFICIAL_SIGNATURE_HASH = "YOUR_REAL_SIGNATURE_HASH_FROM_LOGCAT"
    private const val OFFICIAL_PACKAGE_NAME = "com.yourcompany.lookatme"
    // ...
}
```

### 3. Generating the Final APK

-   Select the desired **Build Variant** in Android Studio (e.g., `persianRelease` or `englishRelease`).
-   Go to **Build > Generate Signed Bundle / APK...**.
-   Select **APK**, choose your release keystore, enter your passwords, and click **Finish**.

The final, secure, and publish-ready APK will be located in the `app/build/outputs/apk/[flavor]/release/` directory.

---

## 🤝 Support

If you like this project, please consider showing your support. It helps motivate further development and open-source contributions!

[☕ Buy Me a Coffee](https://www.buymeacoffee.com/your_username) *(Replace with your link)*

---

Created with ❤️ by GeekNeuron
