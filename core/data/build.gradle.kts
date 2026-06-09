plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.pcmallcompose.core.data"
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.rxjava3)
    implementation(libs.paging.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    // Retrofit
    implementation(libs.retrofit)
}