plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.pcmallcompose.core.database"
    compileSdk {
        version = release(37)
    }
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava3)
    implementation(libs.room.paging)

    // Fastjson
    implementation(libs.fastjson)
}