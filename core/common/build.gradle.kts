plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.pcmallcompose.core.common"
    compileSdk {
        version = release(36)
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}