plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fisei.uta.edu.ec.addressbookapp"
    compileSdk { version = release(37) }

    defaultConfig {
        applicationId = "fisei.uta.edu.ec.addressbookapp"
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
    implementation(project(":presentation"))
    implementation(project(":infrastructure"))
    implementation(project(":application"))
    implementation(libs.appcompat)
    implementation(libs.fragment)
}
