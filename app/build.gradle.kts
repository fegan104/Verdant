import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.kotlin.compose)
}

// Create a variable called keystorePropertiesFile, and initialize it to your
// keystore.properties file, in the rootProject folder.
val keystorePropertiesFile = rootProject.file("keystore.properties")
// Initialize a new Properties() object called keystoreProperties.
val keystoreProperties = Properties()
// Load your keystore.properties file into the keystoreProperties object.
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val IMGUR_CLIENT_ID = if (hasProperty("IMGUR_CLIENT_ID")) "\"${property("IMGUR_CLIENT_ID")}\"" else "\"Define Imgur Client ID\""
val IMGUR_CLIENT_SECRET = if (hasProperty("IMGUR_CLIENT_SECRET")) "\"${property("IMGUR_CLIENT_SECRET")}\"" else "\"Define Imgur Client Secret\""

android {
    namespace = "com.frankegan.verdant"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.frankegan.verdant"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "IMGUR_CLIENT_ID", IMGUR_CLIENT_ID)
        buildConfigField("String", "IMGUR_CLIENT_SECRET", IMGUR_CLIENT_SECRET)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = file(System.getProperty("user.home") + keystoreProperties["storeFile"])
            storePassword = keystoreProperties["storePassword"].toString()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.browser)
    ksp(libs.androidx.room.compiler)
    implementation(libs.zoomable)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil3.coil.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)
    implementation(libs.subsampling.scale.image.view.androidx)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.palette)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}