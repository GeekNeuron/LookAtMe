plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yourcompany.lookatme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yourcompany.lookatme"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ✅ بخش جدید برای تعریف طعم‌ها
    flavorDimensions += "version"
    productFlavors {
        create("persian") {
            dimension = "version"
            applicationIdSuffix = ".fa" // پسوند پکیج‌نیم برای نسخه فارسی
        }
        create("english") {
            dimension = "version"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Glide for loading images and animated GIFs
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
