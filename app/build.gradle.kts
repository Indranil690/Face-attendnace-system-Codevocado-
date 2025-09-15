plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.faceattendanceapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.faceattendanceapp"
        minSdk = 24
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
}

dependencies {
    // ✅ AndroidX and Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")

    // ✅ Google Material
    implementation("com.google.android.material:material:1.12.0")

    // ✅ Retrofit and Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // ✅ Location Services
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // ✅ Google ML Kit Face Detection (Correct version!)
    implementation("com.google.mlkit:face-detection:16.1.5")
    implementation("com.google.mlkit:vision-common:17.3.0")

    // ✅ CameraX (for live camera preview)
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-extensions:1.3.0")

    // ZXing for QR scanning (ZXingScannerView + core)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.2")

    // ✅ Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
