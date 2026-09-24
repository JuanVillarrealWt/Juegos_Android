plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fisei.uta.edu.ec.twittersearchesapp"
    compileSdk { version = release(37) }

    defaultConfig {
        applicationId = "fisei.uta.edu.ec.twittersearchesapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes { release { optimization { enable = false } } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":infrastructure"))
    implementation(project(":presentation"))

    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.coordinatorlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
