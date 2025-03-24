plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // If you're using Firebase

}

android {
    namespace = "com.example.musicalinstrumentsaggregator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.musicalinstrumentsaggregator"
        minSdk = 21
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    // Ensure Java & Kotlin match (fixes "Inconsistent JVM-target" error)
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
    // ----- AndroidX Libraries -----
    // Pick the newer Core KTX version (v1101) and remove duplicates
    implementation(libs.androidx.core.ktx.v1101)

    // Use the AppCompat 1.6.1 entry (remove any other duplicates)
    implementation(libs.androidx.appcompat.v161)

    // Material 1.9.0
    implementation(libs.material.v190)

    // Activity KTX
    implementation(libs.androidx.activity.ktx)

    // ConstraintLayout 2.1.4
    implementation(libs.androidx.constraintlayout.v214)

    // ----- Firebase -----
    implementation(platform(libs.firebase.bom.v3200))
    implementation(libs.google.firebase.firestore.ktx)

    //Coil
    implementation(libs.coil)
    implementation(libs.androidx.activity)

//   Gson
    implementation(libs.gson)
}
