plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.iscg7427groupmobileapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.iscg7427groupmobileapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:32.3.1")) // this is used for logging by Google
    implementation("com.google.android.gms:play-services-auth:20.7.0") // this is also used for google, Google Play services library
    // firebase authentication
    implementation(libs.firebase.auth)
    // firebase realtime database
    implementation(libs.firebase.database)
    // firebase storage
    implementation(libs.firebase.storage)
    // Chart creator
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}