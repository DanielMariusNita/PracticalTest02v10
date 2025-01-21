plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "ro.pub.cs.systems.eim.practicaltest02v10"
    compileSdk = 35

    defaultConfig {
        applicationId = "ro.pub.cs.systems.eim.practicaltest02v10"
        minSdk = 27
        targetSdk = 34
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // Glide dependency
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.firebase.messaging)
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    // OkHttp dependency
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
// Firebase dependencies
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}