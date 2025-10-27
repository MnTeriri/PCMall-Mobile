plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)

    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.pcmallcompose"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.pcmallcompose"
        minSdk = 24
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
            //signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
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
    implementation(libs.androidx.ui.material.icons)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)//传统 View 系统 Material
    implementation(libs.navigation.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation(libs.constraintlayout.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.paging.runtime)
    implementation(libs.paging.rxjava3) // optional - RxJava3 support
    implementation(libs.paging.compose) // optional - Jetpack Compose integration

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)// If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    implementation(libs.room.ktx)// optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.rxjava3)// optional - RxJava3 support for Room
    implementation(libs.room.paging)// optional - Paging 3 Integration

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.retrofit2.converter.jackson)
    implementation(libs.retrofit2.converter.scalars)
    implementation(libs.retrofit2.adapter.rxjava3)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")

    implementation(libs.fastjson)
    implementation(libs.hutool)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.glide)
    ksp(libs.glide.compiler)
    implementation(libs.glide.compose)

    implementation("com.github.f0ris.sweetalert:library:1.6.2")

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}