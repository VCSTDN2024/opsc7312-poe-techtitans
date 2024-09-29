plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fusion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fusion"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Access API_KEY securely
        val apiKey = findProperty("API_KEY")?.toString() ?: "b6544cc7e3f043ba8063aaedbb84cb9e"
        buildConfigField("String", "API_KEY", "\"${apiKey}\"") // Correct interpolation
    }

    // Enable custom BuildConfig generation
    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.core.ktx)
    implementation(libs.junit.junit)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.idling.concurrent)
    annotationProcessor(libs.compiler)
    implementation(libs.androidx.cardview)
    implementation(libs.material.v150)
    implementation(libs.androidx.viewpager2)
    implementation(libs.okhttp)
    implementation(libs.material.v190)

    // Glide for image loading
    implementation(libs.github.glide.v4151)
    annotationProcessor(libs.compiler.v4151)

    // For Activity Result APIs
    implementation(libs.androidx.activity.ktx)

    // Logging interceptor for OkHttp
    implementation(libs.logging.interceptor)

    // Firebase Authentication
    implementation(platform(libs.google.firebase.bom))
    implementation (libs.google.firebase.auth.ktx)

    // Kotlin Coroutines
    implementation (libs.kotlinx.coroutines.android)
    // Espresso dependencies for UI testing
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents.v361)
    androidTestImplementation(libs.androidx.espresso.contrib.v361)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.runner) // Ensure this is included
    androidTestImplementation(libs.androidx.espresso.idling.resource) // Optional: For handling async tasks

    // Unit testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    // Firebase Auth Testing
    implementation(libs.com.google.firebase.firebase.auth.ktx)

    // Ensure Firebase dependencies are included in the test configurations
    androidTestImplementation(libs.firebase.auth)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.hamcrest.hamcrest.library)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core.v351.x2)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.androidx.espresso.core)
}
