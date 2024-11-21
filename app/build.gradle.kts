plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")

    // id("com.google.dagger.hilt.android") version "2.41" apply false
}

android {
    namespace = "com.test.weatherapp1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.test.weatherapp1"
        minSdk = 33
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
    buildTypes.forEach {
        it.buildConfigField(
            "String",
            "WEATHER_API_KEY",
            (project.properties["WEATHER_API_KEY"] ?: "").toString()
        )
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Chuck HTTP inspector
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)

    //Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.android.gradle.plugin)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.moshi.kotlin.codegen)

    // Coil
    implementation(libs.coil)

}