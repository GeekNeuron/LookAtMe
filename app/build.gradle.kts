plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.geekneuron.lookatme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.geekneuron.lookatme"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // This ensures the BuildConfig file is generated
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    flavorDimensions += "version"
    productFlavors {
        create("persian") {
            dimension = "version"
            applicationIdSuffix = ".fa"
            // This adds a variable to the code to identify the Persian build
            buildConfigField("String", "FORCED_LOCALE", "\"fa\"")
        }
        create("english") {
            dimension = "version"
            // This adds a variable to the code to identify the English build
            buildConfigField("String", "FORCED_LOCALE", "\"en\"")
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
