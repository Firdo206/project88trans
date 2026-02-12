plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // ✅ plugin untuk Firebase
}

android {
    namespace = "com.example.project88trans"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.project88trans"
        minSdk = 28
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android UI & Navigation
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Retrofit (API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // RecyclerView (List data)
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Glide (Image loader)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.activity)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0")

    //pdf
    implementation("com.itextpdf:itextg:5.5.10")


    // Unit testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

