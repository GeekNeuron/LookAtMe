# LookAtMe

A simple and fun Android application that acts as a transparent overlay on your home screen, revealing custom content after a unique interaction.

![LookAtMe Screenshot/GIF](https://via.placeholder.com/800x450.png?text=Add+Your+Screenshot+or+GIF+Here)
*(Replace the URL above with a link to your own screenshot or GIF)*

---

## ✨ Features

- **Transparent Overlay:** Runs as a transparent layer on top of the device's launcher.
- **Long-Press Unlock:** Unlocks by holding down on the screen for a configurable duration.
- **Dynamic Content:** Display a title, a central image, a footer, and a vertically scrolling text.
- **Image Support:** Natively supports both static (PNG) and animated (GIF) images.
- **Customizable Audio:** Play a background sound with configurable delay and looping.
- **Custom Font:** Apply a single custom font to all text elements.
- **Developer-Centric:** Fully configurable via simple boolean flags and constants directly in the code.

---

## 📂 Project Structure

Here is the location of the key files you'll need to modify:

```
LookAtMe/
└── app/
    ├── build.gradle.kts
    └── src/
        └── main/
            ├── java/com/example/lookatme/
            │   └── MainActivity.kt       <- Main configuration file
            ├── res/
            │   ├── drawable/
            │   │   ├── animated_image.gif  <- Your animated image
            │   │   └── your_png_image.png  <- Your static image
            │   ├── font/
            │   │   └── my_custom_font.ttf  <- Your custom font
            │   ├── layout/
            │   │   └── activity_main.xml   <- Text content file
            │   ├── raw/
            │   │   └── background_sound.mp3<- Your background sound
            │   └── values/
            │       └── themes.xml
            └── AndroidManifest.xml
```

---

## 🚀 Getting Started

1.  Clone this repository to your local machine.
2.  Open the project in Android Studio.
3.  Android Studio will prompt you to sync the project. Click **Sync Now**. This will automatically download the required `Glide` library as defined in `app/build.gradle.kts`.
4.  Customize the app using the guide below.
5.  Build and run the app on an emulator or a physical device.

---

## ⚙️ How to Customize (Developer's Guide)

All customizations are done by either replacing files or changing values in `MainActivity.kt` and `activity_main.xml`.

### 1. Replacing Content Files

To change the visual and audio assets, simply replace the following files with your own, keeping the filenames the same.

| Content Type        | File Location                | Instructions                                 |
| ------------------- | ---------------------------- | -------------------------------------------- |
| **Custom Font** | `app/src/main/res/font/`     | Replace `my_custom_font.ttf` with your font file.   |
| **Background Sound**| `app/src/main/res/raw/`      | Replace `background_sound.mp3` with your audio file.|
| **Animated Image** | `app/src/main/res/drawable/` | Replace `animated_image.gif` with your GIF.        |
| **Static Image** | `app/src/main/res/drawable/` | Replace `your_png_image.png` with your PNG.      |

### 2. Changing Displayed Text

To change the text that appears on the screen, open the `app/src/main/res/layout/activity_main.xml` file and edit the `android:text` attribute for the desired element.

```xml
<TextView
    android:id="@+id/title_text"
    ...
    android:text="Your New Title Here" />

<TextView
    android:id="@+id/footer_text"
    ...
    android:text="Your new footer text." />

<TextView
    android:id="@+id/scrolling_text"
    ...
    android:text="Your new long scrolling message..." />
```

### 3. Configuring Features in `MainActivity.kt`

Open `app/src/main/java/com/example/lookatme/MainActivity.kt`. At the top of the file, inside the `companion object`, you will find all the settings to control the app's behavior.

#### Feature Toggles
Set these to `true` to enable a feature or `false` to disable it completely.

```kotlin
private const val IS_TITLE_ENABLED = true
private const val IS_IMAGE_ENABLED = true
private const val IS_FOOTER_ENABLED = true
private const val IS_SCROLLING_TEXT_ENABLED = true
private const val IS_SOUND_ENABLED = true
private const val IS_VIBRATION_ENABLED = true
```

#### Image Type
Choose whether to display the animated GIF or the static PNG.

```kotlin
// true: displays animated_image.gif
// false: displays your_png_image.png
private const val USE_ANIMATED_IMAGE = true
```

#### Unlock Mechanism
Change the number of seconds the user must press and hold to unlock.

```kotlin
private const val UNLOCK_DELAY_SECONDS = 9
```

#### Background Sound
Control the sound's delay and looping behavior.

```kotlin
// Delay in seconds before the sound starts playing
private const val SOUND_DELAY_SECONDS = 2

// true: the sound will loop forever
// false: the sound will play only once
private const val SOUND_SHOULD_LOOP = true
```

---

## ⚙️ Finalizing Your App for Release

Before publishing your app, you need to configure some essential details like the app icon, package name, and versioning.

### 1. App Icon 🖼️

The app icon is the image that users will see on their home screen. The easiest and recommended way to set this is by using Android Studio's built-in "Asset Studio".

1.  In the Android Studio project panel (on the left), right-click on the `app` folder.
2.  Navigate to **New** > **Image Asset**.
3.  The **Asset Studio** window will open.
    - In the **Source Asset** section, under **Path**, choose the path to your own source image file (preferably a 1024x1024 pixel PNG).
    - You can use the options in the **Options** section to resize and adjust the background color.
4.  Click **Next**, and then **Finish**.

Android Studio will automatically generate icons in all the required sizes and place them in the correct `res/mipmap` folders.

### 2. Package Name 📦

The package name (or Application ID) is your app's **unique identifier** on the Google Play Store and on Android devices. **This cannot be changed after you publish your app.**

You can set this ID in your app-level `build.gradle.kts` file.

1.  Open the `app/build.gradle.kts` file.
2.  Inside the `defaultConfig` block, modify the `applicationId` property. The standard convention is to use a reverse domain name format.

```kotlin
// In app/build.gradle.kts
android {
    // ...
    defaultConfig {
        // ⚠️ IMPORTANT: Change this to your unique ID before publishing!
        // It should be unique on the Google Play Store.
        applicationId = "com.yourcompany.lookatme"
        
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    // ...
}
```

---

## 📦 Generating an APK File

After you have customized the app, you will need to generate an APK file to install it on a device or to publish it on the Google Play Store. There are two types of APKs you can generate.

### Method 1: Generating a Debug APK (For Testing) 🧪

This is the quickest way to create an APK for testing purposes or for sharing with friends. This APK is signed with a temporary debug key and **cannot** be published on the Google Play Store.

1.  In the Android Studio top menu, go to **Build**.
2.  Select **Build Bundle(s) / APK(s)**.
3.  Click on **Build APK(s)**.
4.  Android Studio will start building the project. Once it's finished, a notification will appear in the bottom-right corner.
5.  Click the **locate** link in the notification to find your APK file. It is usually located at `app/build/outputs/apk/debug/app-debug.apk`.

### Method 2: Generating a Signed/Release APK (For Google Play) 🚀

This is the official version of your app that you will upload to the Google Play Store. It must be digitally signed with your own private key.

1.  In the Android Studio top menu, go to **Build**.
2.  Select **Generate Signed Bundle / APK...**.
3.  In the new window, select **APK** and click **Next**.
4.  You will now see the **Keystore** screen. This is the most important step.
    -   **If you don't have a keystore:** Click on **Create new...**.
        -   **Key store path:** Choose a location on your computer to save your keystore file (e.g., a file named `my-app-key.jks`).
        -   **⚠️ IMPORTANT:** Back up this file securely! If you lose this key, you will **never** be able to update your app on the Google Play Store again.
        -   Fill in the passwords for your keystore and your key alias. Keep these passwords safe.
        -   Fill in the certificate information.
        -   Click **OK**.
    -   **If you already have a keystore:** Choose **Choose existing...** and select your keystore file.
5.  Enter your keystore and key alias passwords and click **Next**.
6.  Select the **Build Variant** as **release**.
7.  Click **Finish**.

Once the build is complete, a notification will appear. Click the **locate** link to find your signed APK file, usually located at `app/release/app-release.apk`. This is the file you will upload to Google Play.

---

## 🤝 Supporting the Developer

If you like this project and find it useful, please consider showing your support. A small gesture, like buying a coffee, goes a long way in motivating further development and open-source contributions!

[☕ Buy Me a Coffee](https://www.buymeacoffee.com/your_username) *(Replace with your actual link)*

---

Created with ❤️ by GeekNeuron
