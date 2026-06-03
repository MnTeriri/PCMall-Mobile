plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.pcmallcompose"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.pcmallcompose"
        minSdk = 26
        targetSdk = 37
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

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3:1.5.0-alpha20")
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.layout)
    implementation(libs.androidx.material3.adaptive.navigation)
    implementation(libs.androidx.ui.material.icons)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.navigation.compose) // Jetpack Compose Integration
    implementation(libs.navigation.fragment) // Fragments Integration
    implementation(libs.navigation.ui) // Views Integration
    implementation(libs.kotlinx.serialization.json)// JSON serialization library, works with the Kotlin serialization plugin

    implementation(libs.paging.runtime)
    implementation(libs.paging.rxjava3) // optional - RxJava3 support
    implementation(libs.paging.compose) // optional - Jetpack Compose integration

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)// If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    implementation(libs.room.ktx)// optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.rxjava3)// optional - RxJava3 support for Room
    implementation(libs.room.paging)// optional - Paging 3 Integration

    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    implementation(libs.retrofit)
    implementation(libs.okhttp.sse)
    implementation(libs.retrofit.converter.jackson)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")

    implementation(libs.glide)
    ksp(libs.glide.compiler)
    implementation(libs.glide.compose)

    implementation(libs.fastjson)
    implementation(libs.hutool)

    implementation(libs.markdown.renderer.android)
    implementation(libs.markdown.renderer.m3)

    implementation("com.github.f0ris.sweetalert:library:1.6.2")
    implementation("com.github.gzu-liyujiang.AndroidPicker:AddressPicker:4.1.15")
}