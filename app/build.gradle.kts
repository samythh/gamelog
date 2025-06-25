
import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

    plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.gamescatalog"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gamescatalog"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.gamescatalog"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 1. Mengambil nilai dari variabel "API_KEY" yang ada di file local.properties.
        val apiKey = localProperties.getProperty("API_KEY")

        // 2. Membuat field konstanta di dalam file BuildConfig yang akan dibuat otomatis.
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }
}

dependencies {

    // --- CORE & UI (JETPACK COMPOSE) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- ARSITEKTUR MVVM ---
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- NAVIGASI ---
    implementation(libs.androidx.navigation.compose)

    // --- KONEKSI API (RETROFIT) ---
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    // DITAMBAHKAN: Logging Interceptor untuk melihat request/response API (sangat berguna untuk debugging)
    implementation(libs.okhttp.logging.interceptor)


    // --- DATABASE LOKAL (ROOM) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- PENYIMPANAN PREFERENSI (DATASTORE) ---
    implementation(libs.androidx.datastore.preferences)

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.coil.compose)

    implementation(libs.androidx.material.icons.extended)


    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)


    implementation(libs.gson)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.accompanist.swiperefresh)
}